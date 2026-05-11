import {Component, inject, OnInit} from '@angular/core';
import { TableModule } from 'primeng/table';
import {CoursesService} from './courses.service';
import {Course} from './models/course.model';

@Component({
  selector: 'app-courses',
  imports: [TableModule],
  providers: [CoursesService],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  private coursesService = inject(CoursesService);
  courses!: Course[];

  ngOnInit() {
    this.coursesService.getCourses().subscribe((data)=>{
      this.courses = data;
    })
  }
}
