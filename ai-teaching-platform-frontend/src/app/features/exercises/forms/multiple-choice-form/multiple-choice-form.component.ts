import {Component, Input, signal} from '@angular/core';
import {InputText} from 'primeng/inputtext';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {Chips, ChipsAddEvent, ChipsRemoveEvent} from 'primeng/chips';
import {Select} from 'primeng/select';
import {Textarea} from 'primeng/textarea';

@Component({
  selector: 'app-multiple-choice-form',
  imports: [
    InputText,
    ReactiveFormsModule,
    Chips,
    Select,
    Textarea
  ],
  templateUrl: './multiple-choice-form.component.html',
  styleUrl: './multiple-choice-form.component.scss'
})
export class MultipleChoiceFormComponent {

  options = signal<String[]>([]);

  @Input({required:true}) form!: FormGroup;

  onOptionAdd(event:ChipsAddEvent){
    this.options.update(options => [...options, event.value]);
  }

  onOptionRemove(event:ChipsRemoveEvent){
    this.options.update(options => options.filter(option => option !== event.value));
  }

}
