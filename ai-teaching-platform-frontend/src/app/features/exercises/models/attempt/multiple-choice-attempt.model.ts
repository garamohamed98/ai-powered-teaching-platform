import {Attempt} from './attempt.dto';

export interface MultiChoiceAttempt extends Attempt{
  answer: String;
}
