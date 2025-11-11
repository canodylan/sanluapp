import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { UserListComponent } from '../../components/user-list/user-list.component';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, UserListComponent],
  template: `
    <mat-toolbar color="primary">SanLuApp - Main Menu</mat-toolbar>
    <div style="padding:16px">
      <h3>Users</h3>
      <app-user-list></app-user-list>
    </div>
  `
})
export class MainComponent {}
