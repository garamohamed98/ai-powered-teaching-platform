import {Component, inject, Input} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Button} from 'primeng/button';
import {Card} from 'primeng/card';
import {Textarea} from 'primeng/textarea';
import {Chips} from 'primeng/chips';

@Component({
  selector: 'app-fill-in-blank-form',
  imports: [
    ReactiveFormsModule,
    Button,
    Card,
    Textarea,
    Chips
  ],
  templateUrl: './fill-in-blank-form.component.html',
  styleUrl: './fill-in-blank-form.component.scss'
})
export class FillInBlankFormComponent {

  @Input({required:true}) form!: FormGroup;
  @Input({required: true}) createSentence!: () => FormGroup;

  get sentences():FormArray{
    return this.form.get('sentences') as FormArray;
  }

  addSentences(){
    this.sentences.push(this.createSentence())
  }

  removeSentences(index: number) {
    this.sentences.removeAt(index);
  }


}
