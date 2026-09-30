import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {Card} from "primeng/card";
import {Editor, EditorTextChangeEvent} from "primeng/editor";
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';
import {Toolbar} from 'primeng/toolbar';
import {Skeleton} from 'primeng/skeleton';
import {Message} from 'primeng/message';
import {Lesson} from '../../models/lesson.model';
import {LessonsService} from '../../services/lessons.service';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-lesson-editor',
  imports: [
    Card,
    Editor,
    FormsModule,
    Button,
    Toolbar,
    Skeleton,
    Message,
  ],
  templateUrl: './lesson-editor.component.html',
  styleUrl: './lesson-editor.component.scss'
})
export class LessonEditorComponent {

  private lessonsService = inject(LessonsService);
  private messageService = inject(MessageService);

  @Input() lesson: Lesson | null = null;
  @Input({required:true}) loading!: boolean;
  @Output() onCourseContentChanged = new EventEmitter<string>();

  handleTextChange(event: EditorTextChangeEvent){
    if(event.source == "user"){
      this.onCourseContentChanged.emit(event.htmlValue);
      console.log("what is printing: ",event.htmlValue);
    }
  }

  handleSaveLesson(){
    if(!this.lesson?.content) return;
    console.log("onSaveCourse event triggered",this.lesson.content);
    if(!this.lesson) return;
    this.lessonsService.updateLessonContent(this.lesson.id,this.lesson.content).subscribe(
      {
        next: (data:any)=>{
          console.log("Lesson content updated content successfully");
          this.lesson = data;
        }
      }
    );
  }

}
