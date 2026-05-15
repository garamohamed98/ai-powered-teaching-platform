import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {Dialog} from "primeng/dialog";
import {InputText} from "primeng/inputtext";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";

@Component({
  selector: 'app-courses-form-modal',
    imports: [
        Button,
        Dialog,
        InputText,
        ReactiveFormsModule
    ],
  templateUrl: './courses-form-modal.component.html',
  styleUrl: './courses-form-modal.component.scss'
})
export class CoursesFormModalComponent {
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
    console.log(this.form.invalid);
    console.log(this.form.value)
  }

}


