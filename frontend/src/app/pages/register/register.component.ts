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
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
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

  goToAdmin(): void {
    if (this.loading) return;
    this.router.navigate(['/admin']);
  }
}
