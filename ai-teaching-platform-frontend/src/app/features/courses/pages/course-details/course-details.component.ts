import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CoursesService} from '../../courses.service';
import {Course} from '../../models/course.model';
import {CourseEditorComponent} from '../../components/course-editor/course-editor.component';
import {delay} from 'rxjs';

@Component({
  selector: 'app-course-details',
  imports: [
    FormsModule,
    CourseEditorComponent,
  ],
  providers: [CoursesService],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent implements OnInit {
  private coursesService = inject(CoursesService);
  private router = inject(Router);

  course = signal<Course>({id:"",title:"",content:""});
  loading = signal<boolean>(false);

  constructor(private route: ActivatedRoute) {}

  ngOnInit():void {
    const courseId = this.route.snapshot.paramMap.get('id');
    console.log("courseId", courseId);
    if(!courseId){
      void this.router.navigate(['/not-found']);
      return;
    }
    this.course.update(currentCourse => ({...currentCourse, id : courseId }));
    this.loadCourse();

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


  handleCourseContentChanged(event:string){
    this.course.update(currentCourse=>({...currentCourse, content: event}));
  }
}
