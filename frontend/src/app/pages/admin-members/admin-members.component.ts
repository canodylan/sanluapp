import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { UserListComponent } from '../../components/user-list/user-list.component';

@Component({
  selector: 'app-admin-members',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, UserListComponent],
  templateUrl: './admin-members.component.html',
  styleUrls: ['./admin-members.component.scss']
})
export class AdminMembersComponent {}
