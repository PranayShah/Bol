package in.uchneech.bol;


import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ThoughtHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public View mView;
    public ThoughtHolder(View v) {
        super(v);
        mView = v;
        mView.findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
