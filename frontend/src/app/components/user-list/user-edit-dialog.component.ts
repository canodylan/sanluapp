import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { User, UserService, RoleOption } from '../../services/user.service';

export interface UserEditDialogData {
  user: User;
}

@Component({
  selector: 'app-user-edit-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatChipsModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './user-edit-dialog.component.html',
  styleUrls: ['./user-edit-dialog.component.scss']
})
export class UserEditDialogComponent {
  private dialogRef = inject(MatDialogRef<UserEditDialogComponent>);
  readonly data = inject<UserEditDialogData>(MAT_DIALOG_DATA);
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  form = this.fb.group({
    username: [this.data.user.username ?? '', Validators.required],
    email: [this.data.user.email ?? '', [Validators.required, Validators.email]],
    nickname: [this.data.user.nickname ?? ''],
    name: [this.data.user.name ?? ''],
    firstName: [this.data.user.firstName ?? ''],
    lastName: [this.data.user.lastName ?? ''],
    phoneNumber: [this.data.user.phoneNumber ?? ''],
    roles: [this.data.user.roles ?? [], Validators.required]
  });

  roleOptions: RoleOption[] = [];
  rolesLoading = false;
  rolesError: string | null = null;

  constructor() {
    this.loadRoles();
  }

  get rolesControl() {
    return this.form.controls.roles;
  }

  save(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.value);
  }

  cancel(): void {
    this.dialogRef.close();
  }

  reloadRoles(): void {
    this.loadRoles();
  }

  getRoleDisplayName(roleName: string): string {
    const normalized = this.normalizeRole(roleName);
    const option = this.roleOptions.find((opt) => this.normalizeRole(opt.name) === normalized);
    if (option?.displayName) {
      return option.displayName;
    }
    if (option?.name) {
      return this.humanize(option.name);
    }
    return this.humanize(roleName);
  }

  private loadRoles(): void {
    this.rolesLoading = true;
    this.rolesError = null;
    this.userService
      .roles()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (roles) => {
          this.roleOptions = roles;
          this.rolesLoading = false;
          this.syncSelectedRoles();
        },
        error: (error) => {
          console.error('Error loading roles', error);
          this.rolesLoading = false;
          this.rolesError = error?.error?.message ?? 'No se pudieron cargar los roles';
        }
      });
  }

  private syncSelectedRoles(): void {
    const current = this.rolesControl.value ?? [];
    if (!current.length || !this.roleOptions.length) {
      return;
    }

    const filtered = current.filter((role) =>
      this.roleOptions.some((opt) => this.normalizeRole(opt.name) === this.normalizeRole(role))
    );

    if (filtered.length !== current.length) {
      this.rolesControl.setValue(filtered);
    }
  }

  private normalizeRole(value?: string): string {
    if (!value) return '';
    let normalized = value.trim().toUpperCase();
    if (normalized.startsWith('ROLE_')) {
      normalized = normalized.substring(5);
    }
    return normalized;
  }

  private humanize(value?: string): string {
    if (!value) return '';
    return value
      .replace(/^ROLE_/i, '')
      .toLowerCase()
      .split(/[_\s]+/)
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}
