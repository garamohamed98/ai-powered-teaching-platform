import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Listbox, ListboxChangeEvent} from 'primeng/listbox';
import {Lesson} from '../../models/lesson.model';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-lessons-panel',
  imports: [
    Listbox,
    FormsModule
  ],
  templateUrl: './lessons-panel.component.html',
  styleUrl: './lessons-panel.component.scss'
})
export class LessonsPanelComponent implements OnInit {

  @Input({required:true}) lessons!: Lesson[];
  @Output() lessonSelected= new EventEmitter<Lesson>();

  selectedLesson!: Lesson;



  ngOnInit(): void {
    console.log("those are the lessons",this.lessons);
  }


  onLessonSelected(event:ListboxChangeEvent):void{
    this.lessonSelected.emit(event.value);
  }

}
