import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CreateSpendingRequestPayload, FinancialAccount, FinancialCategory, FinancialService } from '../../services/financial.service';
import { TruncateCurrencyPipe } from '../../pipes/truncate-currency.pipe';

@Component({
  selector: 'app-expense-request',
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
    TruncateCurrencyPipe
  ],
  templateUrl: './expense-request.component.html',
  styleUrls: ['./expense-request.component.scss']
})
export class ExpenseRequestComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly financialService = inject(FinancialService);

  readonly submitting = signal(false);
  readonly successMessage = signal<string | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly loadingOptions = signal(false);
  readonly categories = signal<FinancialCategory[]>([]);
  readonly accounts = signal<FinancialAccount[]>([]);
  readonly currencyCode = 'EUR';

  readonly requestForm = this.fb.group({
    description: ['', [Validators.required, Validators.maxLength(255)]],
    amount: this.fb.control<number | null>(null, {
      validators: [Validators.required, Validators.min(0.01)]
    }),
    receiptUrl: ['', [Validators.maxLength(500)]],
    categoryId: this.fb.control<number | null>(null),
    accountId: this.fb.control<number | null>(null)
  });

  ngOnInit(): void {
    this.loadOptions();
  }

  submit(): void {
    if (this.requestForm.invalid || this.submitting()) {
      this.requestForm.markAllAsTouched();
      return;
    }

    const payload = this.toPayload();
    if (!payload) {
      this.errorMessage.set('Revisa los datos ingresados antes de enviar.');
      return;
    }

    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.submitting.set(true);

    this.financialService
      .createSpendingRequest(payload)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Tu solicitud fue enviada. Un administrador la revisará en breve.');
          this.requestForm.reset();
        },
        error: (err) => {
          const message = err?.error?.message ?? 'No pudimos registrar tu solicitud, intenta nuevamente.';
          this.errorMessage.set(message);
        }
      });
  }

  private loadOptions(): void {
    this.loadingOptions.set(true);
    forkJoin({
      categories: this.financialService.categories(),
      accounts: this.financialService.accounts()
    })
      .pipe(finalize(() => this.loadingOptions.set(false)))
      .subscribe({
        next: ({ categories, accounts }) => {
          this.categories.set(categories);
          this.accounts.set(accounts);
        },
        error: () => {
          this.errorMessage.set('No pudimos cargar las categorías o cuentas disponibles.');
        }
      });
  }

  private toPayload(): CreateSpendingRequestPayload | null {
    const raw = this.requestForm.getRawValue();
    const amount = Number(raw.amount);
    if (!Number.isFinite(amount) || amount <= 0) {
      return null;
    }
    const description = raw.description?.trim() ?? '';
    const payload: CreateSpendingRequestPayload = {
      description,
      amount,
      receiptUrl: raw.receiptUrl?.trim() ? raw.receiptUrl.trim() : null,
      categoryId: raw.categoryId ?? null,
      accountId: raw.accountId ?? null
    };
    if (!payload.description) {
      return null;
    }
    return payload;
  }
}
