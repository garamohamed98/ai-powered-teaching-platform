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
    UserDropdownMenuComponent
  ],
  templateUrl: './top-bar.component.html',
  styleUrl: './top-bar.component.scss'
})
export class TopBarComponent {
}
