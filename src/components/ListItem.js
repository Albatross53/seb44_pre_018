import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ItemView from 'components/global/questionItem/ItemView';
import ItemAnswer from 'components/global/questionItem/ItemAnswer';
import TagList from 'components/global/tag/TagList';
import AnswerDropdown from 'components/global/answerdetail/AnswerDropdown';
import { styled } from 'styled-components';

const Item = styled.li`
  .itemTitle {
    display: block;
    position: relative;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    span {
      position: relative;

      &::after {
        content: '';
        width: 0;
        height: 12px;
        position: absolute;
        left: 0;
        bottom: -2px;
        background-color: #c2d3ff;
        opacity: 0.4;
        z-index: 0;
        transition: all 0.5s;
        z-index: -1;
      }
    }
  }

  :hover {
    .itemTitle {
      span {
        &::after {
          width: 100%;
        }
      }
    }
  }
`;

const ListText = styled.p`
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.25rem;
  padding: 0;
  * {
    font-size: inherit !important;
    line-height: inherit !important;
    font-weight: inherit !important;
  }
  > *:not(:first-child) {
    display: none;
  }
`;

const ListItem = ({ value }) => {
  const navigate = useNavigate();
  // const {
  //   user: {
  //     userInfo: { member_id },
  //   },
  // } = useSelector((state) => state);
  const toggleEditMode = () => {
    navigate(`/update/${value.questionId}`);
  };

  const handleDeleteAnswer = async () => {
    // try {
    //   const result = await axios.delete(`/delete~/${member_id}/${content_id}`);
    //   console.log(result);
    //   afterDelete();
    // } catch (err) {
    //   console.log('err', err);
    // }
    // navigate(`/question/delete/4/1`);
    // 딜리트요청
    // 리스트 새로고침.
  };

  return (
    <Item className="transition border-b-[1px] border-black/[.3] border-solid cursor-pointer py-3 relative">
      <AnswerDropdown
        onEditAnswer={toggleEditMode}
        onDeleteAnswer={handleDeleteAnswer}
      />
      <Link
        className="flex items-center justify-between"
        to={`/question/${value.questionId}`}
      >
        <div className="w-4/5">
          <h3 className="itemTitle text-base ">
            <span>{value.title}</span>
          </h3>
          <ListText
            className="text-sm font-light py-2"
            dangerouslySetInnerHTML={{ __html: value.content }}
          ></ListText>
          <TagList />
        </div>
        <div className="w-1/5 text-right flex align-center justify-end">
          <ItemView viewCount={value.view} />
          <ItemAnswer />
        </div>
      </Link>
    </Item>
  );
};

export default ListItem;
