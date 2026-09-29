import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CoursesService} from '../../courses.service';
import {Course} from '../../models/course.model';
import {CourseEditorComponent} from '../../components/course-editor/course-editor.component';
import {LessonsPanelComponent} from '../../components/lessons-panel/lessons-panel.component';
import {Lesson} from '../../models/lesson.model';
import {Button} from 'primeng/button';
import {LessonsFormModalComponent} from '../../components/lessons-form-modal/lessons-form-modal.component';

@Component({
  selector: 'app-course-details',
  imports: [
    FormsModule,
    CourseEditorComponent,
    LessonsPanelComponent,
    Button,
    LessonsFormModalComponent,
  ],
  providers: [CoursesService],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent implements OnInit {
  private coursesService = inject(CoursesService);
  private router = inject(Router);

  course = signal<Course>({id:"",title:"",content:""});
  lessons = signal<Lesson[]>([])
  maxLessonsRow: number = 6;
  loading = signal<boolean>(false);
  isCreateLessonModalVisible = signal<boolean>(false);
  courseId!:string;

  constructor(private route: ActivatedRoute) {}

  ngOnInit():void {
    const courseId = this.route.snapshot.paramMap.get('id');
    if(!courseId){
      void this.router.navigate(['/not-found']);
      return;
    }

    this.courseId = courseId;
    this.course.update(currentCourse => ({...currentCourse, id : courseId }));
    this.loadLessons();

  }

  loadCourse(){
    if (this.course().id == "") return;
    this.loading.set(true);
    this.coursesService.getCourseDetails(this.course().id)
      .subscribe({
      next: (data: Course) => {
        console.log("Course details fetched successfully.");
        this.course.set(data);
        this.loading.set(false);
      },
      error: (error) => {
        console.log("An error appeared during fetching course details: ",error);
        this.loading.set(false);

      }
    });
  }

  loadLessons(){
    if (this.courseId == "") return;
    this.loading.set(true);
    this.lessons.set(Array(this.maxLessonsRow));
    this.coursesService
      .getLessonsByCourseId(this.courseId)
      .subscribe({
        next: (data: Lesson[]) => {
          console.log("Lesson details fetched successfully.");
          this.lessons.set(data);
          this.loading.set(false);
          this.lessons().forEach((lesson: Lesson) => {
            console.log("lessons are",lesson);
          })
        },
        error: (error) => {
          this.loading.set(false);
          this.lessons.set([]);
          console.log(
            "An error appeared during fetching lessons: ",
            error.message
          )
        }
      })

  }


  handleCourseContentChanged(event:string){
    this.course.update(currentCourse=>({...currentCourse, content: event}));
  }

  showCreateLessonModal(){
    this.isCreateLessonModalVisible.set(true);
  }
}
