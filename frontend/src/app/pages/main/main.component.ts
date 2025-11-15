import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule, MatCardModule],
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent {
  authService = inject(AuthService);
  router = inject(Router);
  user = this.authService.getCurrentUser();
  isLoggingOut = false;

  get isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  logout(): void {
    if (this.isLoggingOut) return;
    this.isLoggingOut = true;
    this.authService
      .logout()
      .subscribe({
        next: () => this.router.navigate(['/login']),
        error: () => this.router.navigate(['/login'])
      });
  }
}
