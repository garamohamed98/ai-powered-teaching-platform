import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Editor, EditorModule} from 'primeng/editor';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {Toolbar} from 'primeng/toolbar';
import {CoursesService} from '../../courses.service';
import {Course} from '../../models/course.model';

@Component({
  selector: 'app-course-details',
  imports: [
    Editor,
    FormsModule,
    Button,
    Card,
    Toolbar,
  ],
  providers: [CoursesService],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent implements OnInit {
  courseId: string | null = null;
  text: string | undefined;
  private coursesService = inject(CoursesService);
  course!: Course;

  constructor(private route: ActivatedRoute) {}

  ngOnInit():void {
    this.courseId = this.route.snapshot.paramMap.get('id');

    if (this.courseId) {
      this.coursesService.getCourseDetails(this.courseId).subscribe((data: any) => {
        this.course = data;
        this.text = data.content;
        console.log(this.course.title);
      });
    }


  }

  onSaveCourse(){
   if(this.courseId != null && this.text != undefined) {
     console.log("onSaveCourse event triggered",this.text)
     this.coursesService.updateCourseContent(this.courseId,this.text).subscribe((data:any)=>{
       this.course = data;
       this.text = data.content;
     });
   }
  }
}
