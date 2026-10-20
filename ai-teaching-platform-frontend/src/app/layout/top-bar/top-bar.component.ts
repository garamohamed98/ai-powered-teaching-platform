import { Component } from '@angular/core';
import {Menubar} from 'primeng/menubar';
import {MenuItem, PrimeTemplate} from 'primeng/api';
import {Menu} from 'primeng/menu';
import {Button} from 'primeng/button';
import {UserDropdownMenuComponent} from '../../features/users';

@Component({
  selector: 'app-top-bar',
  imports: [
    Menubar,
    PrimeTemplate,
    Menu,
    Button,
    UserDropdownMenuComponent
  ],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.scss'
})
export class TopBarComponent {
  userItems: MenuItem[] = [
    { label: 'Login', icon: 'pi pi-user' },
    { label: 'Signup', icon: 'pi pi-cog' },
  ];
}
