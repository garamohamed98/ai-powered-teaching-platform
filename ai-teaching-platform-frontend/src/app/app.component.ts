import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {CoursesComponent} from './features/courses/courses.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CoursesComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'ai-teaching-platform-frontend';
}
