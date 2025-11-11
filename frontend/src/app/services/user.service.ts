import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id?: number;
  username: string;
  passwordHash?: string;
  email: string;
  nickname?: string;
  name?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  birthday?: string;
  joinAt?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/users`;

  list(): Observable<User[]> {
    return this.http.get<User[]>(this.base);
  }
}
