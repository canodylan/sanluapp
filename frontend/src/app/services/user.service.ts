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

export interface RoleOption {
  id?: number;
  name: string;
  displayName?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/users`;
  private rolesEndpoint = `${environment.apiUrl}/roles`;

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.base);
  }

  get(id: number): Observable<User> {
    return this.http.get<User>(`${this.base}/${id}`);
  }

  update(id: number, payload: Partial<User>): Observable<User> {
    const body: Record<string, unknown> = { ...payload };
    if (payload.roles) {
      body['roles'] = payload.roles.map((role) => ({ name: role }));
    }
    return this.http.put<User>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  roles(): Observable<RoleOption[]> {
    return this.http.get<RoleOption[]>(this.rolesEndpoint);
  }
}
