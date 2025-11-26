import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { DeviceService } from '../../services/device.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatInputModule, MatButtonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;
  error: string | null = null;
  private readonly deviceService = inject(DeviceService);
  readonly isMobile$ = this.deviceService.isMobile$;

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
