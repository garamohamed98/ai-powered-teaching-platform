import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CoursesService} from '../../courses.service';
import {Lesson} from '../../models/lesson.model';
import {LessonsTableComponent} from '../../components/lessons-table/lessons-table.component';
import {CourseExercisesListComponent} from '../../../exercises';

@Component({
  selector: 'app-lessons',
  imports: [
    LessonsTableComponent,
    CourseExercisesListComponent
  ],
  providers: [CoursesService],
  templateUrl: './lessons.component.html',
  styleUrl: './lessons.component.scss'
})
export class LessonsComponent implements OnInit {

  private coursesService = inject(CoursesService);

  lessons = signal<Lesson[]>([])
  maxRows:number = 6;
  loading = signal<boolean>(false);
  courseId!: string;

  constructor(private route:ActivatedRoute) {
  }

  ngOnInit() {
    const courseId = this.route.snapshot.params['id'];
    this.courseId = courseId;
    this.loadLessons(courseId);
  }

  loadLessons(courseId: string){
    if(courseId == "") return;
    this.loading.set(true);
    this.lessons.set(Array(this.maxRows).fill({}));
    this.coursesService
      .getLessonsByCourseId(courseId)
      .subscribe(
      {
        next: (data:Lesson[]) => {
          console.log("Lessons list fetched successfully");
          this.lessons.set(data);
          this.loading.set(false);
        },
        error: (error) => {
          this.loading.set(false);
          this.lessons.set([]);
          console.log(
            "An error appeared during lesson list fetching:",
            error.message
          );
        }
      }
    )

  }

}
