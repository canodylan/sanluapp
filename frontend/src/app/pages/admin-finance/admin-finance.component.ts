import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { FinancialAccount, FinancialCategory, FinancialService, FinancialTransaction, SpendingRequest } from '../../services/financial.service';
import { TruncateCurrencyPipe } from '../../pipes/truncate-currency.pipe';

type RequestAssignment = {
  categoryId: number | null;
  accountId: number | null;
};

@Component({
  selector: 'app-admin-finance',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, MatCardModule, MatIconModule, MatButtonModule, MatChipsModule, MatFormFieldModule, MatSelectModule, MatInputModule, TruncateCurrencyPipe],
  templateUrl: './admin-finance.component.html',
  styleUrls: ['./admin-finance.component.scss']
})
export class AdminFinanceComponent implements OnInit {
  private readonly financialService = inject(FinancialService);
  private readonly fb = inject(FormBuilder);
  readonly currencyCode = 'EUR';
  readonly uncategorizedKey = 'UNCATEGORIZED';
  readonly defaultCategoryColor = '#90a4ae';

  loading = false;
  error: string | null = null;

  primaryAccount: FinancialAccount | null = null;
  wallets: FinancialAccount[] = [];
  transactions: FinancialTransaction[] = [];
  filteredTransactions: FinancialTransaction[] = [];
  categories: FinancialCategory[] = [];
  selectedCategory: string | null = null;
  spendingRequests: SpendingRequest[] = [];
  approvingRequestId: number | null = null;
  updatingAssignmentId: number | null = null;
  categoryModalOpen = false;
  savingCategory = false;
  deletingCategoryId: number | null = null;
  private readonly assignmentSelections = new Map<number, RequestAssignment>();

  readonly categoryForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(60)]],
    color: ['#1976d2']
  });

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;
    forkJoin([
      this.financialService.accounts(),
      this.financialService.transactions(),
      this.financialService.spendingRequests(),
      this.financialService.categories()
    ])
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: ([accounts, transactions, requests, categories]) => {
          const primary = accounts.find((account) => account.primary);
          const fallbackAccounts = [...accounts].sort((a, b) => b.balance - a.balance);
          this.primaryAccount = primary ?? fallbackAccounts[0] ?? null;
          this.wallets = accounts.filter((account) => !account.primary && account.id !== this.primaryAccount?.id);
          this.transactions = transactions;
          this.categories = this.sortCategories(categories);
          this.applyCategoryFilter(this.selectedCategory);
          this.spendingRequests = requests;
          this.initializeAssignments(requests);
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo cargar la información financiera';
        }
      });
  }

  applyCategoryFilter(category: string | null): void {
    this.selectedCategory = category || null;
    if (!this.selectedCategory) {
      this.filteredTransactions = [...this.transactions];
      return;
    }
    if (this.selectedCategory === this.uncategorizedKey) {
      this.filteredTransactions = this.transactions.filter((transaction) => !transaction.categoryId);
      return;
    }
    const categoryId = Number(this.selectedCategory);
    if (Number.isNaN(categoryId)) {
      this.filteredTransactions = [...this.transactions];
      return;
    }
    this.filteredTransactions = this.transactions.filter((transaction) => transaction.categoryId === categoryId);
  }

  clearCategoryFilter(): void {
    this.applyCategoryFilter(null);
  }

  transactionTypeLabel(type: FinancialTransaction['type']): string {
    switch (type) {
      case 'INCOME':
        return 'Ingreso';
      case 'EXPENSE':
        return 'Gasto';
      default:
        return 'Transferencia';
    }
  }

  transactionChipColor(type: FinancialTransaction['type']): 'primary' | 'accent' | 'warn' {
    switch (type) {
      case 'INCOME':
        return 'primary';
      case 'EXPENSE':
        return 'warn';
      default:
        return 'accent';
    }
  }

  requestStatusLabel(status: SpendingRequest['status']): string {
    return status === 'APPROVED' ? 'Aprobada' : 'Pendiente';
  }

  requestChipColor(status: SpendingRequest['status']): 'primary' | 'accent' {
    return status === 'APPROVED' ? 'primary' : 'accent';
  }

  approveRequest(request: SpendingRequest): void {
    const assignment = this.assignmentFor(request);
    if (!assignment.accountId) {
      this.error = 'Selecciona la cuenta a la que se imputará el gasto antes de aprobar';
      return;
    }
    this.approvingRequestId = request.id;
    this.financialService
      .approveSpendingRequest(request.id, {
        accountId: assignment.accountId,
        categoryId: assignment.categoryId ?? null
      })
      .pipe(finalize(() => (this.approvingRequestId = null)))
      .subscribe({
        next: (updated) => {
          this.spendingRequests = this.spendingRequests.map((current) =>
            current.id === updated.id ? updated : current
          );
          this.assignmentSelections.set(updated.id, this.assignmentSnapshot(updated));
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo aprobar la solicitud';
        }
      });
  }

  trackByTransaction = (_: number, transaction: FinancialTransaction) => transaction.id;
  trackByAccount = (_: number, account: FinancialAccount) => account.id;
  trackByRequest = (_: number, request: SpendingRequest) => request.id;
  trackByCategory = (_: number, category: FinancialCategory) => category.id;

  accountIcon(type: FinancialAccount['type']): string {
    switch (type) {
      case 'BANK':
        return 'account_balance';
      case 'CASH':
        return 'savings';
      case 'SAVINGS':
        return 'account_balance_wallet';
      case 'INVESTMENT':
        return 'show_chart';
      default:
        return 'account_balance';
    }
  }

  openCategoryModal(): void {
    this.categoryModalOpen = true;
  }

  closeCategoryModal(): void {
    this.categoryModalOpen = false;
    this.categoryForm.reset({ name: '', color: '#1976d2' });
  }

  submitCategory(): void {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      return;
    }
    const payload = this.categoryForm.getRawValue();
    this.savingCategory = true;
    this.financialService
      .createCategory(payload)
      .pipe(finalize(() => (this.savingCategory = false)))
      .subscribe({
        next: (category) => {
          this.categories = this.sortCategories([...this.categories, category]);
          this.categoryForm.reset({ name: '', color: payload.color ?? '#1976d2' });
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo crear la categoría';
        }
      });
  }

  deleteCategory(category: FinancialCategory): void {
    this.deletingCategoryId = category.id;
    this.financialService
      .deleteCategory(category.id)
      .pipe(finalize(() => (this.deletingCategoryId = null)))
      .subscribe({
        next: () => {
          this.categories = this.sortCategories(this.categories.filter((current) => current.id !== category.id));
          if (this.selectedCategory === category.id.toString()) {
            this.clearCategoryFilter();
          }
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo eliminar la categoría';
        }
      });
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

  private sortCategories(categories: FinancialCategory[]): FinancialCategory[] {
    return [...categories].sort((a, b) => a.name.localeCompare(b.name));
  }

  get allAccounts(): FinancialAccount[] {
    const accounts: FinancialAccount[] = [];
    if (this.primaryAccount) {
      accounts.push(this.primaryAccount);
    }
    accounts.push(...this.wallets);
    return accounts;
  }

  assignmentFor(request: SpendingRequest): RequestAssignment {
    return this.assignmentSelections.get(request.id) ?? this.assignmentSnapshot(request);
  }

  onCategoryChange(request: SpendingRequest, categoryId: number | null): void {
    this.persistAssignment(request, { categoryId });
  }

  onAccountChange(request: SpendingRequest, accountId: number | null): void {
    this.persistAssignment(request, { accountId });
  }

  private initializeAssignments(requests: SpendingRequest[]): void {
    this.assignmentSelections.clear();
    requests.forEach((request) => {
      this.assignmentSelections.set(request.id, this.assignmentSnapshot(request));
    });
  }

  private assignmentSnapshot(request: SpendingRequest): RequestAssignment {
    return {
      categoryId: request.categoryId ?? null,
      accountId: request.accountId ?? null
    };
  }

  private persistAssignment(request: SpendingRequest, changes: Partial<RequestAssignment>): void {
    const current = this.assignmentFor(request);
    const next: RequestAssignment = {
      categoryId: changes.categoryId !== undefined ? changes.categoryId : current.categoryId,
      accountId: changes.accountId !== undefined ? changes.accountId : current.accountId
    };
    if (next.categoryId === current.categoryId && next.accountId === current.accountId) {
      return;
    }
    this.assignmentSelections.set(request.id, next);
    this.updatingAssignmentId = request.id;
    this.financialService
      .updateSpendingRequest(request.id, {
        categoryId: next.categoryId ?? null,
        accountId: next.accountId ?? null
      })
      .pipe(finalize(() => (this.updatingAssignmentId = null)))
      .subscribe({
        next: (updated) => {
          this.spendingRequests = this.spendingRequests.map((current) =>
            current.id === updated.id ? updated : current
          );
          this.assignmentSelections.set(updated.id, this.assignmentSnapshot(updated));
        },
        error: (err) => {
          this.error = this.resolveError(err) ?? 'No se pudo actualizar la asignación del gasto';
        }
      });
  }
}
