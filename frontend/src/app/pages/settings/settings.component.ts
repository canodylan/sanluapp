import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { finalize } from 'rxjs/operators';
import { User, UserService } from '../../services/user.service';
import { AuthService, AuthenticatedUser } from '../../services/auth.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);

  readonly form = this.fb.group({
    nickname: [''],
    phoneNumber: [''],
    email: ['', [Validators.required, Validators.email]],
    birthday: ['']
  });

  loadingProfile = true;
  loadingSave = false;
  errorMessage = '';
  successMessage = '';
  currentUserData: User | null = null;
  private currentUserId: number | null = null;
  readonly today = new Date().toISOString().split('T')[0];

  constructor() {
    const authenticated = this.authService.getCurrentUser();
    if (!authenticated?.id) {
      this.loadingProfile = false;
      this.errorMessage = 'No se pudo cargar la información del usuario.';
      this.form.disable();
      return;
    }

    this.currentUserId = authenticated.id;
    this.patchFormFromUser(authenticated);
    this.fetchLatestUserData(authenticated.id);
  }

  get isSubmitDisabled(): boolean {
    return this.loadingSave || this.form.invalid || !this.currentUserData;
  }

  save(): void {
    if (this.isSubmitDisabled || !this.currentUserId || !this.currentUserData) {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
      }
      return;
    }

    this.loadingSave = true;
    this.successMessage = '';
    this.errorMessage = '';

    const { nickname, phoneNumber, email, birthday } = this.form.getRawValue();
    const payload: User = {
      ...this.currentUserData,
      username: this.currentUserData.username,
      email: (email ?? '').trim(),
      nickname: this.normalizeOptionalField(nickname),
      phoneNumber: this.normalizeOptionalField(phoneNumber),
      birthday: birthday ? birthday : null
    };

    this.userService
      .update(this.currentUserId, payload)
      .pipe(finalize(() => (this.loadingSave = false)))
      .subscribe({
        next: (updated) => {
          this.currentUserData = updated;
          this.patchFormFromUser(updated);
          this.form.markAsPristine();
          this.successMessage = 'Guardamos tus cambios correctamente.';
          this.syncAuthenticatedUser(updated);
        },
        error: (err) => {
          const backendMessage = err?.error?.message;
          this.errorMessage = backendMessage ?? 'No pudimos guardar tus cambios.';
        }
      });
  }

  reset(): void {
    if (this.currentUserData) {
      this.patchFormFromUser(this.currentUserData);
      this.form.markAsPristine();
      this.successMessage = '';
      this.errorMessage = '';
    }
  }

  private fetchLatestUserData(userId: number): void {
    this.userService
      .get(userId)
      .pipe(finalize(() => (this.loadingProfile = false)))
      .subscribe({
        next: (user) => {
          this.currentUserData = user;
          this.patchFormFromUser(user);
          this.errorMessage = '';
          if (this.form.disabled) {
            this.form.enable();
          }
        },
        error: (err) => {
          console.error('Error loading user profile', err);
          this.errorMessage = 'No se pudo sincronizar tu información. Inténtalo de nuevo más tarde.';
          this.form.disable();
        }
      });
  }

  private patchFormFromUser(user: Partial<User> | AuthenticatedUser): void {
    this.form.patchValue({
      nickname: user.nickname ?? '',
      phoneNumber: user.phoneNumber ?? '',
      email: user.email ?? '',
      birthday: this.normalizeDate(user.birthday)
    });
  }

  private normalizeOptionalField(value?: string | null): string | null {
    if (value === null || value === undefined) return null;
    const trimmed = value.trim();
    return trimmed ? trimmed : null;
  }

  private normalizeDate(value?: string | null): string {
    if (!value) return '';
    return value.split('T')[0];
  }

  private syncAuthenticatedUser(updated: User): void {
    const current = this.authService.getCurrentUser();
    if (!current) return;
    const merged: AuthenticatedUser = {
      ...current,
      ...updated,
      id: updated.id ?? current.id,
      username: updated.username ?? current.username,
      email: updated.email ?? current.email,
      nickname: updated.nickname ?? current.nickname,
      phoneNumber: updated.phoneNumber ?? current.phoneNumber,
      birthday: updated.birthday ?? current.birthday,
      roles: updated.roles ?? current.roles
    };
    this.authService.saveUser(merged);
  }
}
