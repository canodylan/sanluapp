import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { UserService, User } from '../../services/user.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
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
