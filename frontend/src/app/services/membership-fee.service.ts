import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type MembershipFeeStatus = 'PENDING' | 'CALCULATED' | 'PAID';

export interface MembershipFeeDay {
  id?: number;
  attendanceDate: string;
}

export interface MembershipFeeDiscount {
  id?: number;
  concept: string;
  amount: number;
  createdAt: string;
}

export interface MembershipFeeUserSummary {
  id?: number;
  username?: string;
  nickname?: string;
  firstName?: string;
  lastName?: string;
  displayName?: string;
}

export interface MembershipFee {
  id?: number;
  userId: number;
  year: number;
  daysAttending: number;
  baseAmount: number;
  discountTotal: number;
  finalAmount: number;
  status: MembershipFeeStatus;
  paidAt?: string;
  createdAt?: string;
  updatedAt?: string;
  attendanceDays: MembershipFeeDay[];
  discounts: MembershipFeeDiscount[];
  user?: MembershipFeeUserSummary;
}

export interface CreateMembershipFeePayload {
  userId: number;
  year: number;
  attendanceDates: string[];
}

export interface DiscountPayload {
  concept: string;
  amount: number;
}

@Injectable({ providedIn: 'root' })
export class MembershipFeeService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/membership-fees`;

  create(payload: CreateMembershipFeePayload): Observable<MembershipFee> {
    return this.http.post<MembershipFee>(this.base, payload);
  }

  applyDiscount(id: number, payload: DiscountPayload): Observable<MembershipFee> {
    return this.http.post<MembershipFee>(`${this.base}/${id}/discounts`, payload);
  }

  markAsPaid(id: number, paidAt?: string): Observable<MembershipFee> {
    return this.http.patch<MembershipFee>(`${this.base}/${id}/pay`, paidAt ? { paidAt } : {});
  }

  byUser(userId: number): Observable<MembershipFee[]> {
    return this.http.get<MembershipFee[]>(`${this.base}/user/${userId}`);
  }

  byStatus(status?: MembershipFeeStatus): Observable<MembershipFee[]> {
    const url = status ? `${this.base}?status=${status}` : this.base;
    return this.http.get<MembershipFee[]>(url);
  }

  markAsCalculated(id: number): Observable<MembershipFee> {
    return this.http.patch<MembershipFee>(`${this.base}/${id}/calculate`, {});
  }

  reopen(id: number, resetDiscounts = true): Observable<MembershipFee> {
    return this.http.patch<MembershipFee>(`${this.base}/${id}/reopen`, { resetDiscounts });
  }
}
