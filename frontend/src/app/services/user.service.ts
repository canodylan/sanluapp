import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id?: number;
  username: string;
  email: string;
  nickname?: string;
  name?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  birthday?: string;
  joinAt?: string;
  roles?: string[];
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/users`;

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.base);
  }

  get(id: number): Observable<User> {
    return this.http.get<User>(`${this.base}/${id}`);
  }

  update(id: number, payload: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.base}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
