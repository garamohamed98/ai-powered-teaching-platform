import {Component, inject, OnInit} from '@angular/core';
import {AuthService} from '@auth0/auth0-angular';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  auth = inject(AuthService);
  ngOnInit() {
    this.auth.isLoading$.subscribe(loading => {
      console.log('Auth loading:', loading);
    });

    this.auth.isAuthenticated$.subscribe(authenticated => {
      console.log('Authenticated:', authenticated);
    });

    this.auth.user$.subscribe(user => {
      console.log('User:', user);
    });
  }
}
