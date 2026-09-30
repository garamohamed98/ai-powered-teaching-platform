import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CoursesService} from '../../services/courses.service';
import {Course} from '../../models/course.model';
import {LessonEditorComponent} from '../../components/lesson-editor/lesson-editor.component';
import {LessonsPanelComponent} from '../../components/lessons-panel/lessons-panel.component';
import {Lesson} from '../../models/lesson.model';
import {Button} from 'primeng/button';
import {LessonsFormModalComponent} from '../../components/lessons-form-modal/lessons-form-modal.component';
import {Skeleton} from 'primeng/skeleton';
import {Card} from 'primeng/card';
import {LessonsService} from '../../services/lessons.service';

@Component({
  selector: 'app-course-details',
  imports: [
    FormsModule,
    LessonEditorComponent,
    LessonsPanelComponent,
    Button,
    LessonsFormModalComponent,
    Skeleton,
    Card,
  ],
  providers: [CoursesService],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent implements OnInit {
  private coursesService = inject(CoursesService);
  private lessonsService = inject(LessonsService);
  private router = inject(Router);

  course = signal<Course>({id:"",title:"",content:""});
  lessons = signal<Lesson[]>([])

  maxLessonsRow: number = 6;
  loading = signal<boolean>(false);
  isCreateLessonModalVisible = signal<boolean>(false);
  courseId!:string;

  lessonIsLoading = signal<boolean>(false);
  loadedLesson = signal<Lesson | null>(null);

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

  loadLesson(lessonId:string){
    if (lessonId == "") return;
    this.lessonIsLoading.set(true);
    this.lessonsService.getLesson(lessonId)
      .subscribe({
      next: (data: Lesson) => {
        console.log("lesson fetched successfully.");
        this.loadedLesson.set(data);
        this.lessonIsLoading.set(false);
      },
      error: (error) => {
        console.log("An error appeared during fetching lesson: ",error);
        this.lessonIsLoading.set(false);

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
    this.loadedLesson.update(currentLesson=>{
      if(!currentLesson){
        return currentLesson;
      }
      return ({...currentLesson, content: event});
    });
  }

  showCreateLessonModal(){
    this.isCreateLessonModalVisible.set(true);
  }

  lessonSelected(event:Lesson){
    this.loadLesson(event.id);
  }
}
