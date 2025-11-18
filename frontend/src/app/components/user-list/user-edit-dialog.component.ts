import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { User } from '../../services/user.service';

export interface UserEditDialogData {
  user: User;
}

@Component({
  selector: 'app-user-edit-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './user-edit-dialog.component.html',
  styleUrls: ['./user-edit-dialog.component.scss']
})
export class UserEditDialogComponent {
  private dialogRef = inject(MatDialogRef<UserEditDialogComponent>);
  readonly data = inject<UserEditDialogData>(MAT_DIALOG_DATA);
  private fb = inject(FormBuilder);

  form = this.fb.group({
    username: [this.data.user.username ?? '', Validators.required],
    email: [this.data.user.email ?? '', [Validators.required, Validators.email]],
    nickname: [this.data.user.nickname ?? ''],
    firstName: [this.data.user.firstName ?? ''],
    lastName: [this.data.user.lastName ?? ''],
    phoneNumber: [this.data.user.phoneNumber ?? '']
  });

  save(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.value);
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
