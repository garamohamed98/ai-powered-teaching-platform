import {Component, Input, OnInit} from '@angular/core';
import {MultipleChoiceContent} from '../../../models/exercise-types/multiple-choice.model';
import {ExerciseContent} from '../../../models/exercise-types/exercise-content.model';

@Component({
  selector: 'app-multiple-choice-content',
  imports: [],
  templateUrl: './multiple-choice-content.component.html',
  styleUrl: './multiple-choice-content.component.scss'
})
export class MultipleChoiceContentComponent implements OnInit {
  @Input({required:true}) content!: ExerciseContent;

  multipleChoiceContent! : MultipleChoiceContent;


  ngOnInit(): void {
    this.multipleChoiceContent = this.content as MultipleChoiceContent;
  }

  getFormedOption(options: string[]){
    return options.join(", | ")
  }

}
