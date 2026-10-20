import {Component, inject} from '@angular/core';
import {AuthService} from '@auth0/auth0-angular';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-login',
  imports: [
    Button
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private auth = inject(AuthService);

  login(){
    this.auth.loginWithRedirect(
      {
        appState: {target:'/courses'},
        authorizationParams:{
          prompt:'select_account'
        }
      }
    );
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

  loginWithGoogle(){
    this.auth.loginWithRedirect({
      appState: {target:'/courses'},
      authorizationParams: {
        connection: 'google-oauth2',
        prompt:'select_account'
      }
    })
  }
}
