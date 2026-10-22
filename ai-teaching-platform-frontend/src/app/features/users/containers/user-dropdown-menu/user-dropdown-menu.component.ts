import {Component, inject} from '@angular/core';
import {Menu} from 'primeng/menu';
import {Button} from 'primeng/button';
import {MenuItem} from 'primeng/api';
import {AuthService} from '@auth0/auth0-angular';
import {AsyncPipe} from '@angular/common';
import {Skeleton} from 'primeng/skeleton';

@Component({
  selector: 'app-user-dropdown-menu',
  imports: [
    Menu,
    Button,
    AsyncPipe,
    Skeleton
  ],
  templateUrl: './user-dropdown-menu.component.html',
  styleUrl: './user-dropdown-menu.component.scss'
})
export class UserDropdownMenuComponent {
  auth = inject(AuthService);
  isLoading$ = this.auth.isLoading$;
  isAuthenticated$ = this.auth.isAuthenticated$;
  user$ = this.auth.user$;

  userItems: MenuItem[] = [
    { separator: true },
    {
      label: 'Logout',
      icon: 'pi pi-sign-out',
      styleClass:'logout-menu-item',
      command: () => this.logout() }
  ]

  login(){
    this.auth.loginWithRedirect(
      {
        appState: {target:'/courses'},
        authorizationParams:{
          prompt:'select_account'
        }
      }
    )
  }

  signup(){
    this.auth.loginWithRedirect({
      appState: {target:'/courses'},
      authorizationParams: {
        screen_hint: 'signup',
        prompt: 'select_account'
      }
    });
  }

  logout(): void {
    this.auth.logout({
      logoutParams:{
        returnTo: window.location.origin,
      }
    })
  }
}
