import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { UserService, User } from '../../services/user.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, MatListModule, MatCardModule],
  template: `
    <mat-card>
      <mat-list>
        <mat-list-item *ngFor="let u of users$ | async">
          <h4 matLine>{{u.username}}</h4>
          <p matLine>{{u.email}}</p>
        </mat-list-item>
      </mat-list>
      <p *ngIf="(users$ | async)?.length === 0" style="padding: 16px; color: #999;">
        No users found
      </p>
      <p *ngIf="error" style="padding: 16px; color: red;">
        Error loading users: {{error}}
      </p>
    </mat-card>
  `
})
export class UserListComponent {
  private service = inject(UserService);
  users$: Observable<User[]>;
  error: string | null = null;

  constructor() {
    this.users$ = this.service.list().pipe(
      tap(
        (users) => console.log('Users loaded:', users),
        (error) => {
          console.error('Error loading users:', error);
          this.error = error.message || 'Unknown error';
        }
      )
    );
  }
}
