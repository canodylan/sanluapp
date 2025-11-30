import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { finalize } from 'rxjs/operators';
import {
  AccountPayload,
  FinancialAccount,
  FinancialService,
  TransferPayload
} from '../../services/financial.service';
import { TruncateCurrencyPipe } from '../../pipes/truncate-currency.pipe';

@Component({
  selector: 'app-admin-accounts',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatChipsModule,
    TruncateCurrencyPipe
  ],
  templateUrl: './admin-accounts.component.html',
  styleUrls: ['./admin-accounts.component.scss']
})
export class AdminAccountsComponent implements OnInit {
  private readonly financialService = inject(FinancialService);
  private readonly fb = inject(FormBuilder);

  accounts: FinancialAccount[] = [];
  loading = false;
  error: string | null = null;
  savingAccount = false;
  transferring = false;
  markingPrimaryId: number | null = null;

  readonly accountForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['']
  });

  readonly initialBalanceControl = this.fb.nonNullable.control(0, [Validators.min(0)]);

  readonly transferForm = this.fb.group({
    fromAccountId: [null as number | null, Validators.required],
    toAccountId: [null as number | null, Validators.required],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    description: ['']
  });

  ngOnInit(): void {
    this.loadAccounts();
  }

  get primaryAccount(): FinancialAccount | undefined {
    return this.accounts.find((account) => account.primary);
  }

  loadAccounts(): void {
    this.loading = true;
    this.error = null;
    this.financialService
      .accounts()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (accounts) => {
          this.accounts = this.sortAccounts(accounts);
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudieron cargar las cuentas del club';
        }
      });
  }

  submitAccount(): void {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
      return;
    }
    const payload: AccountPayload = {
      name: this.accountForm.value.name!,
      description: this.accountForm.value.description ?? undefined,
      balance: this.initialBalanceControl.value ?? undefined
    };
    this.savingAccount = true;
    this.error = null;
    this.financialService
      .createAccount(payload)
      .pipe(finalize(() => (this.savingAccount = false)))
      .subscribe({
        next: (account) => {
          this.accounts = this.sortAccounts([...this.accounts, account]);
          this.accountForm.reset({ name: '', description: '' });
          this.initialBalanceControl.reset(0);
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo crear la cuenta';
        }
      });
  }

  markPrimary(account: FinancialAccount): void {
    this.markingPrimaryId = account.id;
    this.financialService
      .markPrimaryAccount(account.id)
      .pipe(finalize(() => (this.markingPrimaryId = null)))
      .subscribe({
        next: (updated) => {
          const updatedAccounts = this.accounts.map((current) =>
            current.id === updated.id ? updated : { ...current, primary: false }
          );
          this.accounts = this.sortAccounts(updatedAccounts);
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo establecer la cuenta principal';
        }
      });
  }

  submitTransfer(): void {
    if (this.transferForm.invalid) {
      this.transferForm.markAllAsTouched();
      return;
    }
    const fromAccountId = this.transferForm.value.fromAccountId!;
    const toAccountId = this.transferForm.value.toAccountId!;
    if (fromAccountId === toAccountId) {
      this.error = 'Selecciona cuentas diferentes para transferir fondos';
      return;
    }
    const payload: TransferPayload = {
      fromAccountId,
      toAccountId,
      amount: Number(this.transferForm.value.amount),
      description: this.transferForm.value.description ?? undefined
    };
    this.transferring = true;
    this.error = null;
    this.financialService
      .transferBetweenAccounts(payload)
      .pipe(finalize(() => (this.transferring = false)))
      .subscribe({
        next: () => {
          this.transferForm.reset();
          this.loadAccounts();
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo realizar la transferencia';
        }
      });
  }

  trackByAccount = (_: number, account: FinancialAccount) => account.id;

  private sortAccounts(accounts: FinancialAccount[]): FinancialAccount[] {
    return [...accounts].sort((a, b) => Number(b.primary) - Number(a.primary));
  }

  private resolveError(err: unknown): string | null {
    if (!err) {
      return null;
    }
    if (typeof err === 'string') {
      return err;
    }
    return (err as any)?.error?.message ?? null;
  }
}
