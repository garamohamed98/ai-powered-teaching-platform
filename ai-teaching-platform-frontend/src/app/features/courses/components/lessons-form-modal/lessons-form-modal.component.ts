import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Button} from 'primeng/button';
import {CoursesService} from '../../courses.service';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-lessons-form-modal',
  imports: [
    Dialog,
    ReactiveFormsModule,
    InputText,
    Button
  ],
  templateUrl: './lessons-form-modal.component.html',
  styleUrl: './lessons-form-modal.component.scss'
})
export class LessonsFormModalComponent {

  private courseService = inject(CoursesService);
  private messageService = inject(MessageService);

  @Input({required:true}) isVisible!:boolean;
  @Input({required:true}) courseId!:string;
  @Output() isVisibleChange = new EventEmitter<boolean>();
  @Output() lessonCreated = new EventEmitter<void>();

  private formBuilder = inject(FormBuilder);
  form = this.formBuilder.group({
    title: ['', [
      Validators.required,
      Validators.minLength(3),
    ]],
  });

  close(){
    this.isVisible = false;
    this.isVisibleChange.emit(false);
  }

  submit(){
    if(!this.form.valid) return;

    const title = this.form.get('title')?.value;
    if(!title || !this.courseId) return;

    this.courseService.createLesson(this.courseId,title).subscribe({
      next:(res)=>{
        console.log("Lesson created successfully");
        this.messageService.add({
          severity: 'success',
          summary: 'Lesson created successfully',
          detail: `New Lesson is created with title ${res.body?.title}`,
          sticky: false,
        });
        this.lessonCreated.emit();
        this.close();
      }
    })
  }
}
