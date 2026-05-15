import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {Dialog} from "primeng/dialog";
import {InputText} from "primeng/inputtext";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {CoursesService} from '../../courses.service';
import {MessageService} from 'primeng/api';

@Component({
  selector: 'app-courses-form-modal',
    imports: [
        Button,
        Dialog,
        InputText,
        ReactiveFormsModule
    ],
  providers: [
    CoursesService
  ],
  templateUrl: './courses-form-modal.component.html',
  styleUrl: './courses-form-modal.component.scss'
})
export class CoursesFormModalComponent {
  private coursesService = inject(CoursesService);
  private messageService = inject(MessageService);

  @Input({required:true}) isVisible!:boolean;
  @Output() isVisibleChange = new EventEmitter<boolean>();

  private formBuilder = inject(FormBuilder);
  form = this.formBuilder.group({
    title: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
      ]
    ]
  })

  close(){
    this.isVisible = false;
    this.isVisibleChange.emit(false);
  }

  submit(){
    if (!this.form.valid) return;

    const title = this.form.get('title')?.value;
    if (!title) return;

    this.coursesService.createCourse(title).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'info',
          summary: 'New course is created successfully.',
          detail: `New Course is created with title ${res.body?.title}`,
          sticky: false,
        });
        this.close();
      }
    });
  }

}


