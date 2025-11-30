import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export type FinancialTransactionType = 'INCOME' | 'EXPENSE' | 'TRANSFER';
export type FinancialAccountType = 'BANK' | 'CASH' | 'SAVINGS' | 'INVESTMENT';

export interface FinancialCategory {
  id: number;
  name: string;
  color?: string | null;
}

export interface FinancialAccount {
  id: number;
  name: string;
  balance: number;
  description?: string | null;
  createdAt?: string | null;
  type?: FinancialAccountType;
  status?: 'OK' | 'OBSERVED';
  primary?: boolean;
}

export interface FinancialTransactionAccount {
  id?: number;
  name?: string | null;
  balance?: number | null;
}

export interface FinancialTransaction {
  id: number;
  type: FinancialTransactionType;
  amount: number;
  description: string;
  categoryId?: number | null;
  categoryName?: string | null;
  categoryColor?: string | null;
  transactionDate?: string | null;
  createdAt?: string | null;
  accountFrom?: FinancialTransactionAccount | null;
  accountTo?: FinancialTransactionAccount | null;
}

export type SpendingRequestStatus = 'PENDING' | 'APPROVED';

export interface SpendingRequest {
  id: number;
  description: string;
  amount: number;
  requestedById?: number | null;
  requestedBy?: string | null;
  requestedAt?: string | null;
  approved: boolean;
  approvedBy?: string | null;
  approvedAt?: string | null;
  receiptUrl?: string | null;
  transactionId?: number | null;
  categoryId?: number | null;
  categoryName?: string | null;
  categoryColor?: string | null;
  accountId?: number | null;
  accountName?: string | null;
  status: SpendingRequestStatus;
}

export interface CreateSpendingRequestPayload {
  description: string;
  amount: number;
  receiptUrl?: string | null;
  categoryId?: number | null;
  accountId?: number | null;
}

export interface UpdateSpendingRequestPayload {
  categoryId?: number | null;
  accountId?: number | null;
}

export interface ApproveSpendingRequestPayload {
  categoryId?: number | null;
  accountId: number;
}

export interface TransactionFilter {
  type?: FinancialTransactionType;
  accountId?: number;
  categoryId?: number;
  createdBy?: number;
  fromDate?: string;
  toDate?: string;
}

export interface AccountPayload {
  name: string;
  description?: string | null;
  balance?: number;
  primary?: boolean;
}

export interface TransferPayload {
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  description?: string;
  transactionDate?: string;
}

export interface CategoryPayload {
  name: string;
  color: string;
}

@Injectable({ providedIn: 'root' })
export class FinancialService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = `${environment.apiUrl}/financial`;

  accounts(): Observable<FinancialAccount[]> {
    return this.http
      .get<ClubAccountResponse[]>(`${this.baseUrl}/accounts`)
      .pipe(map((accounts) => accounts.map((account) => this.toFinancialAccount(account))));
  }

  createAccount(payload: AccountPayload): Observable<FinancialAccount> {
    const body: ClubAccountRequest = {
      name: payload.name,
      description: payload.description,
      currentBalance: payload.balance,
      primary: payload.primary
    };
    return this.http
      .post<ClubAccountResponse>(`${this.baseUrl}/accounts`, body)
      .pipe(map((account) => this.toFinancialAccount(account)));
  }

  updateAccount(id: number, payload: AccountPayload): Observable<FinancialAccount> {
    const body: ClubAccountRequest = {
      name: payload.name,
      description: payload.description,
      currentBalance: payload.balance,
      primary: payload.primary
    };
    return this.http
      .put<ClubAccountResponse>(`${this.baseUrl}/accounts/${id}`, body)
      .pipe(map((account) => this.toFinancialAccount(account)));
  }

  markPrimaryAccount(id: number): Observable<FinancialAccount> {
    return this.http
      .patch<ClubAccountResponse>(`${this.baseUrl}/accounts/${id}/primary`, {})
      .pipe(map((account) => this.toFinancialAccount(account)));
  }

  transactions(filter?: TransactionFilter): Observable<FinancialTransaction[]> {
    let params = new HttpParams();
    if (filter?.type) {
      params = params.set('type', filter.type);
    }
    if (filter?.accountId) {
      params = params.set('accountId', filter.accountId);
    }
    if (filter?.categoryId) {
      params = params.set('categoryId', filter.categoryId);
    }
    if (filter?.createdBy) {
      params = params.set('createdBy', filter.createdBy);
    }
    if (filter?.fromDate) {
      params = params.set('fromDate', filter.fromDate);
    }
    if (filter?.toDate) {
      params = params.set('toDate', filter.toDate);
    }
    return this.http
      .get<MoneyTransactionResponse[]>(`${this.baseUrl}/transactions`, { params })
      .pipe(map((transactions) => transactions.map((transaction) => this.toFinancialTransaction(transaction))));
  }

  spendingRequests(approved?: boolean | null): Observable<SpendingRequest[]> {
    let params = new HttpParams();
    if (approved !== undefined && approved !== null) {
      params = params.set('approved', approved);
    }
    return this.http
      .get<MoneyExpenseResponse[]>(`${this.baseUrl}/expenses`, { params })
      .pipe(map((requests) => requests.map((request) => this.toSpendingRequest(request))));
  }

  createSpendingRequest(payload: CreateSpendingRequestPayload): Observable<SpendingRequest> {
    const requestedBy = this.authService.getCurrentUser()?.id;
    if (!requestedBy) {
      return throwError(() => new Error('No se pudo determinar el usuario autenticado para registrar el gasto'));
    }
    const body: MoneyExpenseRequest = {
      description: payload.description,
      amount: payload.amount,
      receiptUrl: payload.receiptUrl,
      categoryId: payload.categoryId ?? undefined,
      accountId: payload.accountId ?? undefined,
      requestedBy
    };
    return this.http
      .post<MoneyExpenseResponse>(`${this.baseUrl}/expenses`, body)
      .pipe(map((request) => this.toSpendingRequest(request)));
  }

  approveSpendingRequest(id: number, payload: ApproveSpendingRequestPayload): Observable<SpendingRequest> {
    const approvedBy = this.authService.getCurrentUser()?.id;
    if (!approvedBy) {
      return throwError(() => new Error('No se pudo determinar el usuario aprobador'));
    }
    if (!payload?.accountId) {
      return throwError(() => new Error('Debes elegir la cuenta a la que se imputará el gasto'));
    }
    const body = {
      approvedBy,
      accountId: payload.accountId,
      categoryId: payload.categoryId ?? null
    };
    return this.http
      .patch<MoneyExpenseResponse>(`${this.baseUrl}/expenses/${id}/approve`, body)
      .pipe(map((request) => this.toSpendingRequest(request)));
  }

  updateSpendingRequest(id: number, payload: UpdateSpendingRequestPayload): Observable<SpendingRequest> {
    return this.http
      .patch<MoneyExpenseResponse>(`${this.baseUrl}/expenses/${id}/assignment`, payload)
      .pipe(map((request) => this.toSpendingRequest(request)));
  }

  transferBetweenAccounts(payload: TransferPayload): Observable<FinancialTransaction> {
    const createdBy = this.authService.getCurrentUser()?.id;
    if (!createdBy) {
      return throwError(() => new Error('No se pudo determinar el usuario autenticado para registrar la transferencia'));
    }
    const body: MoneyTransactionRequest = {
      type: 'TRANSFER',
      amount: payload.amount,
      description: payload.description,
      accountFromId: payload.fromAccountId,
      accountToId: payload.toAccountId,
      createdBy,
      transactionDate: payload.transactionDate || new Date().toISOString().slice(0, 10)
    };
    return this.http
      .post<MoneyTransactionResponse>(`${this.baseUrl}/transactions`, body)
      .pipe(map((transaction) => this.toFinancialTransaction(transaction)));
  }

  categories(): Observable<FinancialCategory[]> {
    return this.http
      .get<MoneyCategoryResponse[]>(`${this.baseUrl}/categories`)
      .pipe(map((categories) => categories.map((category) => this.toFinancialCategory(category))));
  }

  createCategory(payload: CategoryPayload): Observable<FinancialCategory> {
    return this.http
      .post<MoneyCategoryResponse>(`${this.baseUrl}/categories`, payload)
      .pipe(map((category) => this.toFinancialCategory(category)));
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/categories/${id}`);
  }

  private toFinancialAccount(response: ClubAccountResponse): FinancialAccount {
    return {
      id: response.id,
      name: response.name,
      balance: Number(response.currentBalance ?? 0),
      description: response.description,
      createdAt: response.createdAt,
      type: 'BANK',
      status: 'OK',
      primary: Boolean(response.primary)
    };
  }

  private toFinancialTransaction(response: MoneyTransactionResponse): FinancialTransaction {
    return {
      id: response.id,
      type: response.type,
      amount: Number(response.amount ?? 0),
      description: response.description,
      categoryId: response.category?.id,
      categoryName: response.category?.name ?? 'Sin categoría',
      categoryColor: response.category?.color ?? null,
      transactionDate: response.transactionDate,
      createdAt: response.createdAt,
      accountFrom: this.toTransactionAccount(response.accountFrom),
      accountTo: this.toTransactionAccount(response.accountTo)
    };
  }

  private toTransactionAccount(account?: MoneyTransactionResponseAccount | null): FinancialTransactionAccount | null {
    if (!account) {
      return null;
    }
    return {
      id: account.id ?? undefined,
      name: account.name,
      balance: account.currentBalance != null ? Number(account.currentBalance) : null
    };
  }

  private toSpendingRequest(response: MoneyExpenseResponse): SpendingRequest {
    const requestedBy = this.resolveUserName(response.requestedBy);
    const approvedBy = this.resolveUserName(response.approvedBy);
    const status: SpendingRequestStatus = response.approved ? 'APPROVED' : 'PENDING';
    const categoryId = response.categoryId ?? response.category?.id ?? response.transaction?.category?.id ?? null;
    const categoryName = response.category?.name ?? response.transaction?.category?.name ?? null;
    const categoryColor = response.category?.color ?? response.transaction?.category?.color ?? null;
    const accountId = response.accountId ?? response.account?.id ?? response.transaction?.accountFrom?.id ?? null;
    const accountName = response.account?.name ?? response.transaction?.accountFrom?.name ?? null;
    return {
      id: response.id,
      description: response.description,
      amount: Number(response.amount ?? 0),
      requestedById: response.requestedBy?.id ?? null,
      requestedBy,
      requestedAt: response.createdAt,
      approved: Boolean(response.approved),
      approvedBy,
      approvedAt: response.approvedAt,
      receiptUrl: response.receiptUrl,
      transactionId: response.transactionId,
      categoryId,
      categoryName,
      categoryColor,
      accountId,
      accountName,
      status
    };
  }

  private resolveUserName(user?: UserSummaryDto | null): string | null {
    if (!user) {
      return null;
    }
    return user.displayName ?? user.nickname ?? user.username ?? null;
  }

  private toFinancialCategory(response: MoneyCategoryResponse): FinancialCategory {
    return {
      id: response.id,
      name: response.name,
      color: response.color ?? null
    };
  }
}

interface ClubAccountResponse {
  id: number;
  name: string;
  description?: string | null;
  currentBalance?: number | string | null;
  createdAt?: string | null;
  primary?: boolean | null;
}

interface ClubAccountRequest {
  name: string;
  description?: string | null;
  currentBalance?: number | string | null;
  primary?: boolean | null;
}

interface MoneyTransactionResponse {
  id: number;
  type: FinancialTransactionType;
  amount?: number | string | null;
  description: string;
  category?: MoneyTransactionResponseCategory | null;
  accountFrom?: MoneyTransactionResponseAccount | null;
  accountTo?: MoneyTransactionResponseAccount | null;
  transactionDate?: string | null;
  createdAt?: string | null;
}

interface MoneyTransactionResponseAccount {
  id?: number | null;
  name?: string | null;
  currentBalance?: number | string | null;
}

interface MoneyTransactionResponseCategory {
  id?: number | null;
  name?: string | null;
  color?: string | null;
}

interface MoneyExpenseResponse {
  id: number;
  description: string;
  amount?: number | string | null;
  receiptUrl?: string | null;
  requestedBy?: UserSummaryDto | null;
  approved?: boolean | null;
  approvedBy?: UserSummaryDto | null;
  approvedAt?: string | null;
  createdAt?: string | null;
  categoryId?: number | null;
  category?: MoneyCategoryResponse | null;
  accountId?: number | null;
  account?: ClubAccountResponse | null;
  transactionId?: number | null;
  transaction?: MoneyTransactionResponse | null;
}

interface MoneyExpenseRequest {
  description: string;
  amount: number;
  receiptUrl?: string | null;
  categoryId?: number | null;
  accountId?: number | null;
  requestedBy: number;
}

interface MoneyTransactionRequest {
  type: FinancialTransactionType;
  amount: number;
  description?: string;
  categoryId?: number;
  accountFromId?: number;
  accountToId?: number;
  transactionDate?: string;
  createdBy: number;
  relatedExpenseId?: number;
}

interface UserSummaryDto {
  id?: number;
  username?: string | null;
  nickname?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  displayName?: string | null;
}

interface MoneyCategoryResponse {
  id: number;
  name: string;
  color: string;
}
