import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MembershipFee, MembershipFeeService } from '../../services/membership-fee.service';
import { User, UserService } from '../../services/user.service';

@Component({
  selector: 'app-admin-fees',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './admin-fees.component.html',
  styleUrls: ['./admin-fees.component.scss']
})
export class AdminFeesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private membershipFeeService = inject(MembershipFeeService);
  private destroyRef = inject(DestroyRef);
  private userService = inject(UserService);

  loading = false;
  error: string | null = null;

  pendingFees: MembershipFee[] = [];
  calculatedFees: MembershipFee[] = [];
  paidFees: MembershipFee[] = [];
  allUsers: User[] = [];
  readonly currentYear = new Date().getFullYear();

  discountTargetId: number | null = null;
  discountSaving = false;
  readonly reviewLoading = new Set<number>();
  readonly reopenLoading = new Set<number>();
  readonly payLoading = new Set<number>();

  readonly discountForm = this.fb.nonNullable.group({
    concept: ['', [Validators.required, Validators.minLength(3)]],
    amount: [0, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.loadFees();
  }

  get missingUsers(): User[] {
    const feesByYear = new Set(
      [...this.pendingFees, ...this.calculatedFees, ...this.paidFees]
        .filter((fee) => fee.year === this.currentYear)
        .map((fee) => fee.userId)
    );
    return this.allUsers.filter((user) => !feesByYear.has(user.id ?? -1));
  }

  get pendingMembers(): MembershipFee[] {
    return this.pendingFees.filter((fee) => fee.attendanceDays.length > 0);
  }

  get reviewedMembers(): MembershipFee[] {
    return this.calculatedFees;
  }

  get paidMembers(): MembershipFee[] {
    return this.paidFees;
  }

  get summaryCards() {
    return [
      { label: 'Sin completar', value: this.missingUsers.length, icon: 'pending_actions' },
      { label: 'Pendiente de revisar', value: this.pendingMembers.length, icon: 'rule' },
      { label: 'Revisados', value: this.reviewedMembers.length, icon: 'task_alt' },
      { label: 'Pagados', value: this.paidMembers.length, icon: 'payments' }
    ];
  }

  trackByMember = (_: number, member: MembershipFee) => member.id ?? 0;
  trackByUser = (_: number, user: User) => user.id ?? 0;

  memberName(member: MembershipFee): string {
    return member.user?.displayName ?? `Miembro #${member.userId}`;
  }

  userDisplayName(user: User): string {
    if (user.nickname) {
      return user.nickname;
    }
    const composed = [user.firstName, user.lastName].filter(Boolean).join(' ').trim();
    if (composed) {
      return composed;
    }
    return user.username;
  }

  loadFees(): void {
    this.loading = true;
    forkJoin({
      users: this.userService.list(),
      pending: this.membershipFeeService.byStatus('PENDING'),
      calculated: this.membershipFeeService.byStatus('CALCULATED'),
      paid: this.membershipFeeService.byStatus('PAID')
    })
      .pipe(
        finalize(() => (this.loading = false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: ({ users, pending, calculated, paid }) => {
          this.allUsers = users;
          this.pendingFees = pending.map(this.normalizeFee);
          this.calculatedFees = calculated.map(this.normalizeFee);
          this.paidFees = paid.map(this.normalizeFee);
          this.error = null;
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudieron cargar las cuotas';
        }
      });
  }

  private normalizeFee = (fee: MembershipFee): MembershipFee => ({
    ...fee,
    attendanceDays: fee.attendanceDays ?? [],
    discounts: fee.discounts ?? []
  });

  openDiscount(member: MembershipFee): void {
    if (member.status !== 'PENDING' || !member.id) {
      return;
    }
    this.discountTargetId = member.id;
    this.discountForm.reset({ concept: '', amount: 0 });
  }

  cancelDiscount(): void {
    this.discountForm.reset({ concept: '', amount: 0 });
    this.discountTargetId = null;
  }

  saveDiscount(member: MembershipFee): void {
    if (!member.id || member.status !== 'PENDING' || this.discountForm.invalid) {
      return;
    }
    this.discountSaving = true;
    const payload = this.discountForm.getRawValue();
    this.membershipFeeService
      .applyDiscount(member.id, payload)
      .pipe(
        finalize(() => (this.discountSaving = false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          this.cancelDiscount();
          this.loadFees();
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo aplicar el descuento';
        }
      });
  }

  markAsReviewed(member: MembershipFee): void {
    if (!member.id || member.status !== 'PENDING') {
      return;
    }
    this.setLoading(this.reviewLoading, member.id, true);
    this.membershipFeeService
      .markAsCalculated(member.id)
      .pipe(
        finalize(() => this.setLoading(this.reviewLoading, member.id!, false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => this.loadFees(),
        error: (err) => (this.error = this.resolveError(err) ?? 'No se pudo marcar como revisada')
      });
  }

  reopen(member: MembershipFee): void {
    if (!member.id || member.status !== 'CALCULATED') {
      return;
    }
    this.setLoading(this.reopenLoading, member.id, true);
    this.membershipFeeService
      .reopen(member.id, true)
      .pipe(
        finalize(() => this.setLoading(this.reopenLoading, member.id!, false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => this.loadFees(),
        error: (err) => (this.error = this.resolveError(err) ?? 'No se pudo reabrir la cuota')
      });
  }

  markAsPaid(member: MembershipFee): void {
    if (!member.id || member.status !== 'CALCULATED') {
      return;
    }
    this.setLoading(this.payLoading, member.id, true);
    this.membershipFeeService
      .markAsPaid(member.id)
      .pipe(
        finalize(() => this.setLoading(this.payLoading, member.id!, false)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => this.loadFees(),
        error: (err) => (this.error = this.resolveError(err) ?? 'No se pudo registrar el pago')
      });
  }

  isReviewing(member: MembershipFee): boolean {
    return member.id != null && this.reviewLoading.has(member.id);
  }

  isReopening(member: MembershipFee): boolean {
    return member.id != null && this.reopenLoading.has(member.id);
  }

  isPaying(member: MembershipFee): boolean {
    return member.id != null && this.payLoading.has(member.id);
  }

  private resolveError(err: unknown): string | null {
    if (!err) return null;
    if (typeof err === 'string') return err;
    if (typeof (err as any).error === 'string') return (err as any).error;
    return (err as any)?.error?.message ?? null;
  }

  private setLoading(target: Set<number>, id: number | undefined, value: boolean): void {
    if (!id) {
      return;
    }
    if (value) {
      target.add(id);
    } else {
      target.delete(id);
    }
  }
}
