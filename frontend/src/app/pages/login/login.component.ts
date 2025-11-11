import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatInputModule, MatButtonModule],
  template: `
    <mat-card class="login-card">
      <h2>Login</h2>
      <form (ngSubmit)="login()">
        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Username" [(ngModel)]="username" name="username" />
        </mat-form-field>

        <mat-form-field appearance="outline" style="width:100%">
          <input matInput placeholder="Password" [(ngModel)]="password" name="password" type="password" />
        </mat-form-field>

        <button mat-raised-button color="primary" type="submit">Login</button>
      </form>
    </mat-card>
  `,
  styles: [`.login-card{max-width:420px;margin:40px auto;padding:16px}`]
})
export class LoginComponent {
  username = '';
  password = '';
  constructor(private router: Router) {}

  login() {
    // Basic placeholder login — in real app validate credentials
    this.router.navigate(['/main']);
  }
}
