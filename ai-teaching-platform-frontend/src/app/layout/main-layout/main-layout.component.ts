import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {TopBarComponent} from '../top-bar/top-bar.component';

@Component({
  selector: 'app-main-layout',
  imports: [
    RouterOutlet,
    TopBarComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

}
