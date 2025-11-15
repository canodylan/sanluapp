import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatInputModule, MatButtonModule, RouterModule],
  template: `
    <mat-card class="login-card">
      <h2>Login</h2>
      <form (ngSubmit)="login()">
        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Username" [(ngModel)]="username" name="username" required />
        </mat-form-field>

        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Password" [(ngModel)]="password" name="password" type="password" required />
        </mat-form-field>

        <p class="error" *ngIf="error">{{ error }}</p>

        <button mat-raised-button color="primary" type="submit" [disabled]="loading || !username || !password">
          {{ loading ? 'Entrando...' : 'Login' }}
        </button>
      </form>
      <p class="register-link">
        ¿No tienes cuenta?
        <a routerLink="/register">Crear una cuenta</a>
      </p>
    </mat-card>
  `,
  styles: [`.login-card{max-width:420px;margin:40px auto;padding:16px}.error{color:#c62828;margin:8px 0}.register-link{margin-top:16px}`]
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;
  error: string | null = null;

  constructor(private router: Router, private authService: AuthService) {}

  login() {
    if (!this.username || !this.password || this.loading) return;

    this.error = null;
    this.loading = true;
    this.authService
      .login(this.username, this.password)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => this.router.navigate(['/home']),
        error: (err) => {
          const backendMessage = err?.error?.message;
          this.error = backendMessage ?? 'Credenciales inválidas';
        }
      });
  }
}
