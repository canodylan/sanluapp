import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'sanluapp-frontend';
  themeService = inject(ThemeService);

  ngOnInit() {
    // Initialize theme on app start
    this.themeService.setDarkTheme(this.themeService.getCurrentTheme());
  }
}



