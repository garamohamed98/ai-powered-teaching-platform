import {Component, inject, OnInit, signal} from '@angular/core';
import {UsersService} from '../../services/users.service';
import {AuthService} from '@auth0/auth0-angular';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-user-infos',
  imports: [
    Button
  ],
  templateUrl: './user-infos.component.html',
  styleUrl: './user-infos.component.scss'
})
export class UserInfosComponent implements OnInit {

  private userService = inject(UsersService);
  auth = inject(AuthService);

  currentUser = signal<{id: string,email: string,name:string}>({id:'',email:'',name:''});

  ngOnInit() {
    this.loadCurrentUserInfos();
  }

  loadCurrentUserInfos() {
    this.userService.getUser().subscribe({
      next: data => {
        this.currentUser.set(
          {
            id: data.id,
            email: data.email,
            name: data.name,
          }
        );
      }
    })
  }

  logout() : void {
    this.auth.logout({
      logoutParams:{
        returnTo: window.location.origin
      }
    })
  }
}
