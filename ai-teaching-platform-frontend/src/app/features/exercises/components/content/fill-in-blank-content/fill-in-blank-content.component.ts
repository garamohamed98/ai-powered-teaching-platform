import {Component, Input, OnInit} from '@angular/core';
import {ExerciseContent} from '../../../models/exercise-types/exercise-content.model';
import {FillInBlankContent} from '../../../models/exercise-types/fill-in-blank.model';

@Component({
  selector: 'app-fill-in-blank-content',
  imports: [],
  templateUrl: './fill-in-blank-content.component.html',
  styleUrl: './fill-in-blank-content.component.scss'
})
export class FillInBlankContentComponent implements OnInit {
  @Input({required:true}) content!: ExerciseContent;

  fillInBlankContent! : FillInBlankContent;

  ngOnInit() {
    console.log("this is the content: ", this.content);
    this.fillInBlankContent = this.content as FillInBlankContent;
  }

}
