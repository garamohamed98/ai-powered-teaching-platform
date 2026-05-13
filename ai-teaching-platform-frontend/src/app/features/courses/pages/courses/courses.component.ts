import {Component, inject, OnInit} from '@angular/core';
import {TableModule} from 'primeng/table';
import {CoursesService} from '../../courses.service';
import {Course} from '../../models/course.model';
import {CardModule} from 'primeng/card';
import {Button} from 'primeng/button';
import {RouterLink} from '@angular/router';
import {MessageService} from 'primeng/api';
import {Toast} from 'primeng/toast';

@Component({
  selector: 'app-courses',
  imports: [TableModule, CardModule, Button, RouterLink],
  providers: [CoursesService, MessageService],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  private coursesService = inject(CoursesService);
  private messageService = inject(MessageService);
  loading: boolean = false;
  courses!: Course[];


  ngOnInit() {
    this.loading = true;

    this.coursesService.getCourses()
      .subscribe({
        next: (res)=>{
          this.courses = res;
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          console.log("Error: ",error.message);
        }
      })
  }
}
