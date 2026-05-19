import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {Card} from "primeng/card";
import {Editor, EditorTextChangeEvent} from "primeng/editor";
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {Toolbar} from 'primeng/toolbar';
import {Course} from '../../models/course.model';
import {CoursesService} from '../../courses.service';
import {Skeleton} from 'primeng/skeleton';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-course-editor',
  imports: [
    Card,
    Editor,
    FormsModule,
    Button,
    Toolbar,
    Skeleton,
    Message,
  ],
  templateUrl: './course-editor.component.html',
  styleUrl: './course-editor.component.scss'
})
export class CourseEditorComponent {

  private coursesService = inject(CoursesService);

  @Input({required:true}) course!: Course;
  @Input({required:true}) loading!: boolean;
  @Output() onCourseContentChanged = new EventEmitter<string>();

  handleTextChange(event: EditorTextChangeEvent){
    if(event.source == "user"){
      this.onCourseContentChanged.emit(event.htmlValue);
    }
  }

  handleSaveCourse(){
      console.log("onSaveCourse event triggered",this.course.content)
      this.coursesService.updateCourseContent(this.course.id,this.course.content).subscribe(
        {
          next: (data:any)=>{
            this.course = data;
          }
        }
      );
  }

}
