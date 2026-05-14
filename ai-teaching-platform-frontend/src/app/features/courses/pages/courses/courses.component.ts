import {Component, inject, OnInit} from '@angular/core';
import {TableModule} from 'primeng/table';
import {CoursesService} from '../../courses.service';
import {Course} from '../../models/course.model';
import {CardModule} from 'primeng/card';
import {Button} from 'primeng/button';
import {RouterLink} from '@angular/router';
import {MessageService} from 'primeng/api';
import {Skeleton, SkeletonModule} from 'primeng/skeleton';
import {NgIf} from '@angular/common';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-courses',
  imports: [TableModule, CardModule, Button, RouterLink, SkeletonModule, NgIf, Message],
  providers: [CoursesService, MessageService],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  private coursesService = inject(CoursesService);
  loading: boolean = false;
  maxRows = 10;
  courses!: Course[];


  ngOnInit() {
    this.loading = true;
    this.courses =  Array(this.maxRows).fill({});
    this.coursesService.getCourses()
      .subscribe({
        next: (res)=>{
          this.courses = res;
          this.loading = false;
        },
        error: (error) => {
          this.loading = false;
          this.courses = [];
          console.log("Error: ",error.message);
        }
      })
  }
}
