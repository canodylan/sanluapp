import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';
import { MembershipFee, MembershipFeeService } from '../../services/membership-fee.service';

@Component({
  selector: 'app-quota',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule, MatCardModule, FormsModule, MatIconModule],
  templateUrl: './quota.component.html',
  styleUrls: ['./quota.component.scss']
})
export class QuotaComponent implements OnInit {
  private authService = inject(AuthService);
  private membershipFeeService = inject(MembershipFeeService);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly fees = signal<MembershipFee[]>([]);
  readonly selectedFee = signal<MembershipFee | null>(null);

  readonly year = signal(new Date().getFullYear());
  readonly selectedDates = signal<string[]>([]);
  newDate = '';
  submitting = false;

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user?.id) {
      this.fetchFees(user.id);
    }
  }

  get statusMessage(): string {
    const fee = this.selectedFee();
    if (!fee) return 'Aún no has registrado tu cuota para este año.';
    switch (fee.status) {
      case 'PENDING':
        return 'Tu cuota está pendiente de revisión.';
      case 'CALCULATED':
        return `Tu cuota final es de ${fee.finalAmount.toFixed(2)}€.`;
      case 'PAID':
        return '¡Cuota pagada! Gracias por ponerte al día.';
      default:
        return '';
    }
  }

  addDate(): void {
    if (!this.newDate) return;
    const current = this.selectedDates();
    if (current.includes(this.newDate)) return;
    this.selectedDates.set([...current, this.newDate].sort());
    this.newDate = '';
  }

  removeDate(date: string): void {
    this.selectedDates.set(this.selectedDates().filter((d) => d !== date));
  }

  get canSubmitDates(): boolean {
    return this.selectedDates().length > 0 && !this.submitting;
  }

  submitDates(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.id || !this.canSubmitDates) return;
    const userId = user.id;
    this.submitting = true;
    this.error.set(null);
    this.success.set(null);
    this.membershipFeeService
      .create({
        userId,
        year: this.year(),
        attendanceDates: this.selectedDates()
      })
      .subscribe({
        next: (fee) => {
          this.success.set('Se registraron tus días de asistencia.');
          this.selectedDates.set([]);
          this.submitting = false;
          this.refreshFees(userId, fee);
        },
        error: (err) => {
          this.error.set(err?.error?.message ?? 'No se pudo registrar la cuota.');
          this.submitting = false;
        }
      });
  }

  private fetchFees(userId: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.membershipFeeService.byUser(userId).subscribe({
      next: (fees) => {
        this.loading.set(false);
        this.setFees(fees);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'No se pudieron cargar tus cuotas');
        this.loading.set(false);
      }
    });
  }

  private refreshFees(userId: number, updated: MembershipFee): void {
    const others = this.fees().filter((fee) => fee.id !== updated.id);
    this.setFees([...others, updated]);
    this.fetchFees(userId);
  }

  private setFees(fees: MembershipFee[]): void {
    const sorted = [...fees].sort((a, b) => (b.year - a.year) || (b.createdAt?.localeCompare(a.createdAt ?? '') ?? 0));
    this.fees.set(sorted);
    this.selectedFee.set(sorted[0] ?? null);
  }
}
