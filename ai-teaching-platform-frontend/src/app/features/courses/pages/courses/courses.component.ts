import {Component, inject, OnInit, signal} from '@angular/core';
import {TableModule} from 'primeng/table';
import {CoursesService} from '../../courses.service';
import {Course} from '../../models/course.model';
import {CardModule} from 'primeng/card';
import {Button} from 'primeng/button';
import {SkeletonModule} from 'primeng/skeleton';
import { ReactiveFormsModule} from '@angular/forms';
import {CoursesTableComponent} from '../../components/courses-table/courses-table.component';
import {CoursesFormModalComponent} from '../../components/courses-form-modal/courses-form-modal.component';
import {delay} from 'rxjs';

@Component({
  selector: 'app-courses',
  imports: [TableModule, CardModule, Button, SkeletonModule, ReactiveFormsModule, CoursesTableComponent, CoursesFormModalComponent],
  providers: [CoursesService,],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  private coursesService = inject(CoursesService);

  loading = signal<boolean>(false);
  maxRows:number = 6;
  courses = signal<Course[]>([]);
  isCreateModalVisible= signal<boolean>(false);


  ngOnInit() {
    this.loadCourses();
  };

  loadCourses(){
    this.loading.set(true);
    this.courses.set(Array(this.maxRows).fill({}));
    this.coursesService.getCourses()
      .pipe(
        delay(2000)
      )
      .subscribe({
        next: (res)=>{
          this.courses.set(res);
          this.loading.set(false);
        },
        error: (error) => {
          this.loading.set(false);
          this.courses.set([]);
          console.log("Error: ",error.message);
        }
      })
  }

  showCreateCourseModal(){
    this.isCreateModalVisible.set(true);
  }
}
