import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatInputModule, MatButtonModule],
  template: `
    <mat-card class="register-card">
      <h2>Crear cuenta</h2>
      <form (ngSubmit)="register()">
        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Nombre de usuario" [(ngModel)]="username" name="username" required />
        </mat-form-field>

        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Email" [(ngModel)]="email" name="email" type="email" required />
        </mat-form-field>

        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Nick" [(ngModel)]="nickname" name="nickname" />
        </mat-form-field>

        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Contraseña" [(ngModel)]="password" name="password" type="password" required />
        </mat-form-field>

        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Repetir contraseña" [(ngModel)]="confirmPassword" name="confirmPassword" type="password" required />
        </mat-form-field>

        <p class="error" *ngIf="error">{{ error }}</p>

        <button mat-raised-button color="primary" type="submit" [disabled]="loading">
          {{ loading ? 'Registrando...' : 'Registrarse' }}
        </button>
        <button mat-button type="button" (click)="goToLogin()" style="margin-left:8px">Volver al login</button>
      </form>
    </mat-card>
  `,
  styles: [`.register-card{max-width:480px;margin:32px auto;padding:16px}.error{color:#c62828;margin:8px 0}`]
})
export class RegisterComponent {
  username = '';
  email = '';
  nickname = '';
  password = '';
  confirmPassword = '';
  loading = false;
  error: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    if (this.loading) return;
    if (!this.username || !this.email || !this.password || !this.confirmPassword) {
      this.error = 'Todos los campos son obligatorios';
      return;
    }
    if (this.password !== this.confirmPassword) {
      this.error = 'Las contraseñas no coinciden';
      return;
    }

    this.error = null;
    this.loading = true;

    this.authService
      .register({
        username: this.username,
        email: this.email,
        password: this.password,
        nickname: this.nickname || undefined
      })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => this.router.navigate(['/home']),
        error: (err) => {
          const backendMessage = err?.error?.message;
          this.error = backendMessage ?? 'No se pudo registrar el usuario';
        }
      });
  }

  goToLogin(): void {
    if (this.loading) return;
    this.router.navigate(['/login']);
  }
}
