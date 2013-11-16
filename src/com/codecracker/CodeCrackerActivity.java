package com.codecracker;

import java.util.Random;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class CodeCrackerActivity extends Activity {
	public enum State {
		UNKNOWN(-3), SELECTED(-2), EMPTY(0), BLACK(1), BLUE(2), GREEN(3), LEMON(
				4), ORANGE(5), RED(6);

		private int mValue;

		private State(int value) {
			mValue = value;
		}

		public int getValue() {
			return mValue;
		}

		public static State fromInt(int i) {
			for (State s : values()) {
				if (s.getValue() == i) {
					return s;
				}
			}
			return EMPTY;
		}
	}

	public enum TagState {
		NONERIGHT(0), COLORRIGHT(1), BOTHRIGHT(2);
		private int mValue;

		private TagState(int value) {
			mValue = value;
		}

		public int getValue() {
			return mValue;
		}

		public static TagState fromInt(int i) {
			for (TagState s : values()) {
				if (s.getValue() == i) {
					return s;
				}
			}
			return NONERIGHT;
		}
	}

	int currentSelectedRow;
	int currentSelectedColumn;
	int chance;
	int countPiece;
	int height, width;

	boolean isShown;

	private State[][] mDataPiece = new State[7][4];
	private State[] mDataAnswer = new State[4];
	private TagState[] mTag = new TagState[4];

	private Drawable mDrawableBoard;
	private Drawable mDrawableBoard2;

	private Drawable mDrawableSelected;
	private Drawable mDrawableEmpty;

	private Drawable mDrawableBlack;
	private Drawable mDrawableBlue;
	private Drawable mDrawableGreen;
	private Drawable mDrawableLemon;
	private Drawable mDrawableOrange;
	private Drawable mDrawableRed;

	private Drawable mDrawableWhiteTag;
	private Drawable mDrawableBlackTag;

	private Drawable mDrawableCheck;

	private Drawable mDrawableNewGame;
	private Drawable mDrawableBulbYellow;
	private Drawable mDrawableBulbWhite;

	private AbsoluteLayout.LayoutParams[][] lp = new AbsoluteLayout.LayoutParams[7][4];
	private AbsoluteLayout.LayoutParams[] lpBelow = new AbsoluteLayout.LayoutParams[6];
	private AbsoluteLayout.LayoutParams[] lpAnswer = new AbsoluteLayout.LayoutParams[4];
	private AbsoluteLayout.LayoutParams[][] lpTag = new AbsoluteLayout.LayoutParams[7][4];
	private AbsoluteLayout.LayoutParams[] lpCheck = new AbsoluteLayout.LayoutParams[7];

	AbsoluteLayout abslayout;
	private ImageButton[][] b = new ImageButton[7][4];
	private ImageButton[] buttonBelow = new ImageButton[6];
	private ImageButton[] buttonAnswer = new ImageButton[4];
	private ImageButton[][] tag = new ImageButton[7][4];
	private ImageButton[] check = new ImageButton[7];
	private ImageButton buttonNewGame;
	private ImageButton buttonBulb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDrawableSelected = getResources().getDrawable(R.drawable.lib_cross);
		mDrawableEmpty = getResources().getDrawable(R.drawable.lib_circle);

		mDrawableBlack = getResources().getDrawable(R.drawable.black);
		mDrawableBlue = getResources().getDrawable(R.drawable.blue);
		mDrawableGreen = getResources().getDrawable(R.drawable.green);
		mDrawableLemon = getResources().getDrawable(R.drawable.lemon);
		mDrawableOrange = getResources().getDrawable(R.drawable.orange);
		mDrawableRed = getResources().getDrawable(R.drawable.red);

		mDrawableWhiteTag = getResources().getDrawable(R.drawable.ws);
		mDrawableBlackTag = getResources().getDrawable(R.drawable.bs);

		mDrawableCheck = getResources().getDrawable(R.drawable.help);

		mDrawableNewGame = getResources().getDrawable(R.drawable.start);

		mDrawableBulbYellow = getResources()
				.getDrawable(R.drawable.bulb_yellow);
		mDrawableBulbWhite = getResources().getDrawable(R.drawable.bulb_white);

		mDrawableBoard = getResources().getDrawable(R.drawable.board33);
		mDrawableBoard2 = getResources().getDrawable(R.drawable.board22);

		height = this.getWindowManager().getDefaultDisplay().getHeight();
		width = this.getWindowManager().getDefaultDisplay().getWidth();

		abslayout = new AbsoluteLayout(this);
		setContentView(abslayout);
		abslayout.setBackgroundDrawable(mDrawableBoard);
		abslayout.setId(11);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		initializeGame();
		setLayout();
		// showAnswer();
		addListeners();

	}

	public void addListeners() {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				final int m = i, n = j;
				OnClickListener listenerRegular = new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (m == chance) {
							for (int j2 = 0; j2 < 4; j2++) {
								if (mDataPiece[m][j2].equals(State.SELECTED)) {

									b[m][j2].setImageDrawable(mDrawableEmpty);
									mDataPiece[m][j2] = State.EMPTY;
									currentSelectedRow = -1;
									currentSelectedColumn = -1;
								}
							}
							if (mDataPiece[m][n].equals(State.EMPTY)) {
								b[m][n].setImageDrawable(mDrawableSelected);
								mDataPiece[m][n] = State.SELECTED;
								currentSelectedRow = m;
								currentSelectedColumn = n;
							} else if (mDataPiece[m][n].equals(State.SELECTED)) {
								b[m][n].setImageDrawable(mDrawableEmpty);
								mDataPiece[m][n] = State.EMPTY;
								currentSelectedRow = -1;
								currentSelectedColumn = -1;
							} else {
								countPiece--;
								if (countPiece != 4) {
									check[chance].setImageDrawable(null);
									check[chance].setEnabled(false);
								}
								b[m][n].setImageDrawable(mDrawableSelected);
								mDataPiece[m][n] = State.SELECTED;
								currentSelectedRow = m;
								currentSelectedColumn = n;
							}
						}
					}
				};
				b[m][n].setOnClickListener(listenerRegular);
			}
		}
		for (int i = 0; i < 6; i++) {
			final int j = i;
			OnClickListener listenerBelow = new OnClickListener() {
				@Override
				public void onClick(View v) {
					final int m = currentSelectedRow, n = currentSelectedColumn;
					if (m != -1 && n != -1) {
						if (mDataPiece[m][n].equals(State.SELECTED)) {
							countPiece++;
							mDataPiece[m][n] = State.fromInt(j + 1);
							b[m][n].setImageDrawable(getDrawableFromState(State
									.fromInt(j + 1)));
						}
						if (countPiece == 4) {
							check[chance].setImageDrawable(mDrawableCheck);
							check[chance].setEnabled(true);
						}
					}
				}
			};
			buttonBelow[i].setOnClickListener(listenerBelow);
		}

		for (int i = 0; i < 7; i++) {
			final int j = i;
			OnClickListener listenerCheck = new OnClickListener() {
				@Override
				public void onClick(View v) {
					check[j].setImageDrawable(null);
					check[j].setEnabled(false);

					boolean correct = checkAnswer();
					paintTag();
					if (correct) {
						Toast.makeText(CodeCrackerActivity.this, "You win",
								Toast.LENGTH_SHORT).show();
						showAnswer();
					} else if (chance < 6) {
						chance++;
						countPiece = 0;
					} else {
						for (int m = 0; m < 6; m++) {
							buttonBelow[m].setEnabled(false);
						}
						for (int m = 0; m < 4; m++) {
							b[chance][m].setEnabled(false);
						}
						Toast.makeText(CodeCrackerActivity.this,
								"You have no chance left", Toast.LENGTH_SHORT)
								.show();
						showAnswer();
					}

				}
			};
			check[i].setOnClickListener(listenerCheck);
		}

		OnClickListener listenerNewGame = new OnClickListener() {
			@Override
			public void onClick(View v) {
				initializeGame();
				initializeImage();
			}
		};
		buttonNewGame.setOnClickListener(listenerNewGame);

		OnClickListener listenerBulb = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShown) {
					buttonBulb.setImageDrawable(mDrawableBulbWhite);
					hideAnswer();

				} else {
					buttonBulb.setImageDrawable(mDrawableBulbYellow);
					showAnswer();
				}
				isShown = !isShown;
			}
		};
		buttonBulb.setOnClickListener(listenerBulb);
	}

	public void initializeGame() {
		currentSelectedRow = -1;
		currentSelectedColumn = -1;
		chance = 0;
		countPiece = 0;
		isShown = false;

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				mDataPiece[i][j] = State.EMPTY;
			}
		}
		for (int i = 0; i < 4; i++) {
			mTag[i] = TagState.NONERIGHT;
		}
		generateAnswer();

		// generateSpecificAnswer(State.BLACK,State.BLACK,State.ORANGE,State.ORANGE);
	}

	private void initializeImage() {
		abslayout.setBackgroundDrawable(mDrawableBoard);
		for (int i = 0; i < 4; i++) {
			buttonAnswer[i].setImageDrawable(null);
		}

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				b[i][j].setImageDrawable(mDrawableEmpty);
				b[i][j].setEnabled(true);

				tag[i][j].setImageDrawable(null);
			}
		}
		for (int i = 0; i < 6; i++) {
			buttonBelow[i].setImageDrawable(getDrawableFromState(State
					.fromInt(i + 1)));
			buttonBelow[i].setEnabled(true);
		}

		for (int i = 0; i < 7; i++) {
			check[i].setImageDrawable(null);
			check[i].setEnabled(false);
		}

		buttonNewGame.setImageDrawable(mDrawableNewGame);
		buttonBulb.setImageDrawable(mDrawableBulbWhite);

	}

	public void generateAnswer() {
		Random r = new Random();
		for (int i = 0; i < 4; i++) {
			mDataAnswer[i] = State.fromInt(r.nextInt(6) + 1);
		}
	}

	public void generateSpecificAnswer(State s1, State s2, State s3, State s4) {
		mDataAnswer[0] = s1;
		mDataAnswer[1] = s2;
		mDataAnswer[2] = s3;
		mDataAnswer[3] = s4;
	}

	public void setLayout() {
		int marginLeft = width / 13 * 2;
		int marginTop = height / 25;
		int marginBottom = height / 25;
		int gapVertical = width / 7;
		int gapHorizontal = width / 6;
		int widthTag = width / 20;
		int heightTag = width / 20;

		// layout for the answer on top
		for (int i = 0; i < 4; i++) {
			buttonAnswer[i] = new ImageButton(this);
			buttonAnswer[i].setImageDrawable(null);
			buttonAnswer[i].setBackgroundDrawable(null);

			lpAnswer[i] = new AbsoluteLayout.LayoutParams(width / 6, width / 6,
					gapHorizontal * i + marginLeft, marginTop);
			abslayout.addView(buttonAnswer[i], lpAnswer[i]);
		}

		// layout for regular buttons and tags
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 4; j++) {
				b[i][j] = new ImageButton(this);
				b[i][j].setImageDrawable(mDrawableEmpty);
				b[i][j].setBackgroundDrawable(null);

				lp[i][j] = new AbsoluteLayout.LayoutParams(width / 6,
						width / 6, gapHorizontal * j + marginLeft, gapVertical
								* (i + 1) + marginTop);
				abslayout.addView(b[i][j], lp[i][j]);

				tag[i][j] = new ImageButton(this);
				tag[i][j].setImageDrawable(null);
				tag[i][j].setBackgroundDrawable(null);

				lpTag[i][j] = new AbsoluteLayout.LayoutParams(widthTag,
						heightTag, width * 5 / 6 + j % 2 * widthTag, marginTop
								+ gapVertical * (i + 1) + heightTag * 5 / 10
								+ j / 2 * heightTag);
				abslayout.addView(tag[i][j], lpTag[i][j]);
			}
		}
		// layout for choice buttons below
		for (int i = 0; i < 6; i++) {

			buttonBelow[i] = new ImageButton(this);
			buttonBelow[i].setImageDrawable(getDrawableFromState(State
					.fromInt(i + 1)));
			buttonBelow[i].setBackgroundDrawable(null);

			lpBelow[i] = new AbsoluteLayout.LayoutParams(width / 10,
					width / 10, width / 10 * (i % 6) + width / 11 * 2, height
							- marginBottom - width / 10);
			abslayout.addView(buttonBelow[i], lpBelow[i]);
		}
		// layout for check buttons
		for (int i = 0; i < 7; i++) {

			check[i] = new ImageButton(this);
			check[i].setImageDrawable(null);
			check[i].setBackgroundDrawable(null);
			check[i].setEnabled(false);
			lpCheck[i] = new AbsoluteLayout.LayoutParams(width / 6, width / 6,
					width * 12 / 15, gapVertical * (i + 1) + marginTop);
			abslayout.addView(check[i], lpCheck[i]);
		}

		// layout for new game button
		buttonNewGame = new ImageButton(this);
		buttonNewGame.setImageDrawable(mDrawableNewGame);
		buttonNewGame.setBackgroundDrawable(null);

		abslayout.addView(buttonNewGame, new AbsoluteLayout.LayoutParams(
				width / 6, width / 6, width * 5 / 6, height - marginBottom
						- width / 6));

		// layout for bulb button
		buttonBulb = new ImageButton(this);
		buttonBulb.setImageDrawable(mDrawableBulbWhite);
		buttonBulb.setBackgroundDrawable(null);

		abslayout.addView(buttonBulb, new AbsoluteLayout.LayoutParams(
				width / 8, width / 8, marginLeft / 15, marginTop));
	}

	public boolean checkAnswer() {
		boolean correct = true;
		State[] isMatched = new State[4];

		// initialize tag states
		for (int i = 0; i < 4; i++) {
			isMatched[i] = State.EMPTY;
		}
		for (int i = 0; i < 4; i++) {
			mTag[i] = TagState.NONERIGHT;
		}

		// check pieces both right
		for (int i = 0; i < 4; i++) {
			if (mDataAnswer[i].equals(mDataPiece[chance][i])) {
				mTag[i] = TagState.BOTHRIGHT;
				isMatched[i] = mDataAnswer[i];
			} else
				correct = false;
		}
		if (correct)
			return true;

		// check pieces with just color right
		for (int indexGuess = 0; indexGuess < 4; indexGuess++) {
			if (!mTag[indexGuess].equals(TagState.BOTHRIGHT)) {
				for (int indexAnswer = 0; indexAnswer < 4; indexAnswer++) {
					if (mDataPiece[chance][indexGuess]
							.equals(mDataAnswer[indexAnswer])
							&& !isMatched[indexAnswer]
									.equals(mDataPiece[chance][indexGuess])
							&& isMatched[indexAnswer].equals(State.EMPTY)) {
						mTag[indexGuess] = TagState.COLORRIGHT;
						isMatched[indexAnswer] = mDataAnswer[indexGuess];
						break;
					}
				}
			}
		}
		return false;
	}

	public void paintTag() {
		int countBlack = 0, countWhite = 0;

		for (int i = 0; i < 4; i++) {
			if (mTag[i].equals(TagState.COLORRIGHT))
				countWhite++;
			else if (mTag[i].equals(TagState.BOTHRIGHT))
				countBlack++;
		}
		for (int i = 0; i < 4; i++) {
			if (countBlack > 0) {
				tag[chance][i].setImageDrawable(mDrawableBlackTag);
				countBlack--;
			} else if (countWhite > 0) {
				tag[chance][i].setImageDrawable(mDrawableWhiteTag);
				countWhite--;
			}
		}
	}

	public void showAnswer() {
		for (int i = 0; i < 4; i++) {
			buttonAnswer[i]
					.setImageDrawable(getDrawableFromState(mDataAnswer[i]));
		}
		abslayout.setBackgroundDrawable(mDrawableBoard2);
	}

	public void hideAnswer() {
		for (int i = 0; i < 4; i++) {
			buttonAnswer[i].setImageDrawable(null);
		}
		abslayout.setBackgroundDrawable(mDrawableBoard);
	}

	public Drawable getDrawableFromState(State s) {
		Drawable drawableTemp = null;
		switch (s) {
		case BLACK:
			drawableTemp = mDrawableBlack;
			break;
		case BLUE:
			drawableTemp = mDrawableBlue;
			break;
		case GREEN:
			drawableTemp = mDrawableGreen;
			break;
		case LEMON:
			drawableTemp = mDrawableLemon;
			break;
		case ORANGE:
			drawableTemp = mDrawableOrange;
			break;
		case RED:
			drawableTemp = mDrawableRed;
			break;
		}
		return drawableTemp;
	}
}