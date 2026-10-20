import {Component, inject, OnInit, signal} from '@angular/core';
import {Menu} from 'primeng/menu';
import {Button} from 'primeng/button';
import {MenuItem} from 'primeng/api';
import {AuthService, User} from '@auth0/auth0-angular';
import {UsersService} from '../../services/users.service';
import {AsyncPipe} from '@angular/common';
import {toSignal} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-user-dropdown-menu',
  imports: [
    Menu,
    Button,
    AsyncPipe
  ],
  templateUrl: './user-dropdown-menu.component.html',
  styleUrl: './user-dropdown-menu.component.scss'
})
export class UserDropdownMenuComponent implements OnInit {
  private userService = inject(UsersService);
  auth = inject(AuthService);
  isAuthenticated$ = this.auth.isAuthenticated$;

  private authUser = toSignal(this.auth.user$, {
    initialValue: null
  });

  userItems: MenuItem[] = [
    { separator: true },
    { label: 'Logout', icon: 'pi pi-sign-out', command: () => this.logout() }
  ]

  currentUser = signal<User>({
    id:"",
    name:"",
    email: "",
  })

  ngOnInit() {
    this.loadCurrentUserInfos();
  }

  get userName(){
    return this.authUser()?.name
  }

  loadCurrentUserInfos(){
    this.userService.getUser().subscribe({
      next: data =>{
        this.currentUser.set(
          {
            id: data.id,
            email: data.email,
            name: data.name,
          }
        )
      }
    })
  }

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
    this.currentUser.set(
      {
        id:"",
        name:"",
        email: "",
      }
    )
  }
}
