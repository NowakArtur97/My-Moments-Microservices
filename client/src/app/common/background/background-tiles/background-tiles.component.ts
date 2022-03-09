import { animate, state, style, transition, trigger } from '@angular/animations';
import { AfterViewInit, Component, Input, OnInit } from '@angular/core';

import { BackgroundService } from '../background.service';

@Component({
  selector: 'app-background-tiles',
  templateUrl: './background-tiles.component.html',
  styleUrls: ['./background-tiles.component.css'],
  animations: [
    trigger('move', [
      state(
        'down',
        style({
          transform: 'translateY(-{{tilesTopOffset}}px)',
        }),
        { params: { tilesTopOffset: '0' } }
      ),
      state(
        'up',
        style({
          transform: 'translateY({{tilesTopOffset}}px)',
        }),
        { params: { tilesTopOffset: '0' } }
      ),
      transition('up <=> down', animate('{{randomTiming}}ms'), {
        params: { randomTiming: '0' },
      }),
    ]),
  ],
})
export class BackgroundTilesComponent implements OnInit, AfterViewInit {
  private readonly TILES_ANIMATIONS_STATES = { UP: 'up', DOWN: 'down' };
  private readonly ANIMATIONS_TIMINGS = { MIN: 8000, MAX: 12000 };

  state =
    Math.random() > 0.5
      ? this.TILES_ANIMATIONS_STATES.UP
      : this.TILES_ANIMATIONS_STATES.DOWN;
  tileHeight!: string;
  tilesTopOffset!: number;
  randomTiming!: number;
  animationTimeout!: any;

  @Input() images!: string[];

  constructor(private backgroundService: BackgroundService) {
    this.setupTiles();
  }

  ngOnInit(): void {}

  setupTiles(): void {
    const tileHeight = this.backgroundService.getTileHeight();
    this.tileHeight = `${tileHeight}px`;
    this.tilesTopOffset =
      tileHeight *
      (this.backgroundService.NUMBER_OF_BACKUP_IMAGES_FOR_ANIMATION / 2);
    this.randomTiming = this.generateRandomTiming();
  }

  ngAfterViewInit = (): void => this.restartAnimation();

  onAnimationFinished = (): void => this.restartAnimation();

  private restartAnimation(): void {
    window.clearTimeout(this.animationTimeout);
    this.animationTimeout = setTimeout(() => {
      this.state =
        this.state === this.TILES_ANIMATIONS_STATES.UP
          ? this.TILES_ANIMATIONS_STATES.DOWN
          : this.TILES_ANIMATIONS_STATES.UP;
    }, 0);
  }

  private generateRandomTiming = (): number =>
    Math.floor(
      Math.random() *
        (this.ANIMATIONS_TIMINGS.MAX - this.ANIMATIONS_TIMINGS.MIN + 1) +
        this.ANIMATIONS_TIMINGS.MIN
    );
}
