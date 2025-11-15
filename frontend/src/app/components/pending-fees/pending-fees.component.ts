import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MembershipFee, MembershipFeeService } from '../../services/membership-fee.service';

@Component({
  selector: 'app-pending-fees',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatIconModule],
  templateUrl: './pending-fees.component.html',
  styleUrls: ['./pending-fees.component.scss']
})
export class PendingFeesComponent implements OnInit {
  private membershipFeeService = inject(MembershipFeeService);

  readonly pendingFees = signal<MembershipFee[]>([]);
  readonly selectedFee = signal<MembershipFee | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  discountConcept = '';
  discountAmount: number | null = null;
  submittingDiscount = false;
  markingPaid = false;

  ngOnInit(): void {
    this.loadPending();
  }

  selectFee(fee: MembershipFee): void {
    this.selectedFee.set(fee);
    this.discountConcept = '';
    this.discountAmount = null;
  }

  get canApplyDiscount(): boolean {
    return !!this.discountConcept && !!this.discountAmount && !this.submittingDiscount;
  }

  applyDiscount(): void {
    const fee = this.selectedFee();
    if (!fee || !this.canApplyDiscount) return;
    this.submittingDiscount = true;
    this.membershipFeeService
      .applyDiscount(fee.id!, { concept: this.discountConcept.trim(), amount: this.discountAmount! })
      .subscribe({
        next: (updated) => {
          this.updateFee(updated);
          this.submittingDiscount = false;
          this.discountConcept = '';
          this.discountAmount = null;
        },
        error: (err) => {
          this.error.set(err?.error?.message ?? 'No se pudo aplicar el descuento');
          this.submittingDiscount = false;
        }
      });
  }

  markAsPaid(): void {
    const fee = this.selectedFee();
    if (!fee || this.markingPaid) return;
    this.markingPaid = true;
    this.membershipFeeService
      .markAsPaid(fee.id!)
      .subscribe({
        next: (updated) => {
          this.updateFee(updated);
          this.markingPaid = false;
        },
        error: (err) => {
          this.error.set(err?.error?.message ?? 'No se pudo marcar como pagada');
          this.markingPaid = false;
        }
      });
  }

  loadPending(): void {
    this.loading.set(true);
    this.membershipFeeService.byStatus('PENDING').subscribe({
      next: (fees) => {
        this.pendingFees.set(fees);
        this.selectedFee.set(fees[0] ?? null);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'No se pudieron cargar las cuotas pendientes');
        this.loading.set(false);
      }
    });
  }

  private updateFee(updated: MembershipFee): void {
    this.pendingFees.set(this.pendingFees().filter((fee) => fee.id !== updated.id));
    if (updated.status === 'PENDING') {
      this.pendingFees.set([updated, ...this.pendingFees()]);
    }
    this.selectedFee.set(updated.status === 'PENDING' ? updated : (this.pendingFees()[0] ?? null));
  }
}
