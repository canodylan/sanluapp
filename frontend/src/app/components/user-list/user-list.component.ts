import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize, switchMap, tap } from 'rxjs/operators';
import { UserService, User } from '../../services/user.service';
import { UserEditDialogComponent } from './user-edit-dialog.component';
import { ConfirmDialogComponent } from './confirm-dialog.component';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDialogModule, MatSnackBarModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent {
  private service = inject(UserService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  private refresh$ = new BehaviorSubject<void>(undefined);
  users$: Observable<User[]> = this.refresh$.pipe(
    switchMap(() =>
      this.service.list().pipe(
        tap(() => (this.error = null)),
        catchError((err) => {
          console.error('Error loading users:', err);
          this.error = err?.error?.message ?? 'No se pudieron cargar los usuarios';
          return of([]);
        })
      )
    )
  );

  error: string | null = null;
  deletingId: number | null = null;

  refresh(): void {
    this.refresh$.next();
  }

  onEdit(user: User): void {
    if (!user.id) return;
    const dialogRef = this.dialog.open(UserEditDialogComponent, {
      data: { user },
      width: '420px'
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) return;
      this.service
        .update(user.id!, result)
        .pipe(
          tap(() => {
            this.snackBar.open('Usuario actualizado', 'Cerrar', { duration: 3000 });
            this.refresh();
          }),
          catchError((err) => {
            console.error('Error updating user', err);
            this.snackBar.open(err?.error?.message ?? 'No se pudo actualizar el usuario', 'Cerrar', {
              duration: 4000
            });
            return of(null);
          })
        )
        .subscribe();
    });
  }

  onDelete(user: User): void {
    if (!user.id) return;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Eliminar usuario',
        message: `¿Seguro que deseas eliminar a ${user.nickname || user.username}? Esta acción no se puede deshacer.`
      }
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.deletingId = user.id!;
      this.service
        .delete(user.id!)
        .pipe(
          tap(() => {
            this.snackBar.open('Usuario eliminado', 'Cerrar', { duration: 3000 });
            this.refresh();
          }),
          catchError((err) => {
            console.error('Error deleting user', err);
            this.snackBar.open(err?.error?.message ?? 'No se pudo eliminar el usuario', 'Cerrar', {
              duration: 4000
            });
            return of(null);
          }),
          finalize(() => (this.deletingId = null))
        )
        .subscribe();
    });
  }
}
